package com.g3.sppts.dao;

import com.g3.sppts.model.Entry;
import com.g3.sppts.model.Milestone;
import com.g3.sppts.model.Project;
import com.g3.sppts.util.DBUtil;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Data access for projects and their milestones, updates and feedback. */
public class ProjectDAO {

    private static final String BASE =
            "SELECT p.id, p.owner_id, p.group_name, p.title, p.description, p.tech, p.team, "
          + "p.status, p.submitted, u.name AS owner_name "
          + "FROM projects p JOIN users u ON u.id = p.owner_id ";

    // ---------- reads ----------

    public List<Project> findAll() {
        return query("", new Object[0]);
    }

    public List<Project> findByOwner(int ownerId) {
        return query("WHERE p.owner_id = ? ", new Object[] { ownerId });
    }

    public List<Project> findByStatuses(String... statuses) {
        StringBuilder in = new StringBuilder();
        for (int i = 0; i < statuses.length; i++) in.append(i == 0 ? "?" : ",?");
        return query("WHERE p.status IN (" + in + ") ", statuses);
    }

    /** Loads one project with all milestones, updates and feedback; null if missing. */
    public Project findById(int id) {
        return DBUtil.run(c -> {
            List<Project> list = query(c, "WHERE p.id = ? ", new Object[] { id });
            if (list.isEmpty()) return null;
            Project p = list.get(0);
            p.setUpdates(entries(c,
                    "SELECT id, created, body, NULL AS by_name FROM updates WHERE project_id = ? ORDER BY id DESC", id));
            p.setFeedback(entries(c,
                    "SELECT id, created, body, by_name FROM feedback WHERE project_id = ? ORDER BY id DESC", id));
            return p;
        });
    }

    // ---------- writes ----------

    public int create(Project p) {
        return DBUtil.run(c -> {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO projects (owner_id, group_name, title, description, tech, team, status, submitted) "
                  + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, p.getOwnerId());
                ps.setString(2, p.getGroupName());
                ps.setString(3, p.getTitle());
                ps.setString(4, p.getDescription());
                ps.setString(5, p.getTech());
                ps.setString(6, p.getTeam());
                ps.setString(7, Project.PENDING);
                ps.setDate(8, Date.valueOf(LocalDate.now()));
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return keys.getInt(1);
                }
            }
        });
    }

    public void delete(int id) {
        exec("DELETE FROM projects WHERE id = ?", id);
    }

    public void updateStatus(int id, String status) {
        exec("UPDATE projects SET status = ? WHERE id = ?", status, id);
    }

    /** Faculty review: status change and optional comment are saved in ONE transaction. */
    public void review(int projectId, String status, String reviewer, String comment) {
        DBUtil.inTransaction(c -> {
            try (PreparedStatement ps = c.prepareStatement("UPDATE projects SET status = ? WHERE id = ?")) {
                ps.setString(1, status);
                ps.setInt(2, projectId);
                ps.executeUpdate();
            }
            if (comment != null && !comment.isBlank()) {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO feedback (project_id, created, by_name, body) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, projectId);
                    ps.setDate(2, Date.valueOf(LocalDate.now()));
                    ps.setString(3, reviewer);
                    ps.setString(4, comment.trim());
                    ps.executeUpdate();
                }
            }
            return null;
        });
    }

    public void addMilestone(int projectId, String title, String dueDate) {
        DBUtil.run(c -> {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO milestones (project_id, title, due_date, done) VALUES (?, ?, ?, FALSE)")) {
                ps.setInt(1, projectId);
                ps.setString(2, title);
                if (dueDate == null || dueDate.isBlank()) ps.setNull(3, java.sql.Types.DATE);
                else ps.setDate(3, Date.valueOf(dueDate));
                ps.executeUpdate();
            }
            return null;
        });
    }

    public void toggleMilestone(int projectId, int milestoneId) {
        exec("UPDATE milestones SET done = NOT done WHERE id = ? AND project_id = ?", milestoneId, projectId);
    }

    public void deleteMilestone(int projectId, int milestoneId) {
        exec("DELETE FROM milestones WHERE id = ? AND project_id = ?", milestoneId, projectId);
    }

    public void addUpdate(int projectId, String body) {
        exec("INSERT INTO updates (project_id, created, body) VALUES (?, ?, ?)",
                projectId, Date.valueOf(LocalDate.now()), body);
    }

    // ---------- helpers ----------

    private void exec(String sql, Object... params) {
        DBUtil.run(c -> {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                bind(ps, params);
                ps.executeUpdate();
            }
            return null;
        });
    }

    private List<Project> query(String where, Object[] params) {
        return DBUtil.run(c -> query(c, where, params));
    }

    private List<Project> query(Connection c, String where, Object[] params) throws SQLException {
        List<Project> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(BASE + where + "ORDER BY p.id DESC")) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProject(rs));
            }
        }
        for (Project p : list) p.setMilestones(milestones(c, p.getId()));
        return list;
    }

    private static void bind(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
    }

    private static Project mapProject(ResultSet rs) throws SQLException {
        Project p = new Project();
        p.setId(rs.getInt("id"));
        p.setOwnerId(rs.getInt("owner_id"));
        p.setOwnerName(rs.getString("owner_name"));
        p.setGroupName(rs.getString("group_name"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setTech(rs.getString("tech"));
        p.setTeam(rs.getString("team"));
        p.setStatus(rs.getString("status"));
        p.setSubmitted(rs.getDate("submitted").toString());
        return p;
    }

    private static List<Milestone> milestones(Connection c, int projectId) throws SQLException {
        List<Milestone> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id, project_id, title, due_date, done FROM milestones WHERE project_id = ? ORDER BY id")) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Milestone m = new Milestone();
                    m.setId(rs.getInt("id"));
                    m.setProjectId(rs.getInt("project_id"));
                    m.setTitle(rs.getString("title"));
                    Date d = rs.getDate("due_date");
                    m.setDueDate(d == null ? null : d.toString());
                    m.setDone(rs.getBoolean("done"));
                    list.add(m);
                }
            }
        }
        return list;
    }

    private static List<Entry> entries(Connection c, String sql, int projectId) throws SQLException {
        List<Entry> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Entry e = new Entry();
                    e.setId(rs.getInt("id"));
                    e.setDate(rs.getDate("created").toString());
                    e.setBody(rs.getString("body"));
                    e.setAuthor(rs.getString("by_name"));
                    list.add(e);
                }
            }
        }
        return list;
    }
}
