package com.g3.sppts.servlet;

import com.g3.sppts.dao.ProjectDAO;
import com.g3.sppts.model.Project;
import com.g3.sppts.model.User;
import com.g3.sppts.util.Web;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Main controller for projects.
 * GET  ?action=list|new|view   -> pick data, forward to a JSP
 * POST ?action=create|delete|resubmit|addMilestone|toggleMilestone|deleteMilestone|addUpdate
 *      -> change data, then redirect (Post/Redirect/Get)
 */
@WebServlet("/project")
public class ProjectServlet extends HttpServlet {
    private final ProjectDAO projects = new ProjectDAO();

    // ---------------- GET ----------------

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = Web.currentUser(req);
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "new":
                if (!u.isStudent()) { resp.sendError(403); return; }
                forward(req, resp, "form.jsp");
                break;
            case "view":
                view(req, resp, u);
                break;
            default:
                list(req, resp, u);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp, User u) throws ServletException, IOException {
        List<Project> list;
        String heading;
        if (u.isStudent()) {
            list = projects.findByOwner(u.getId());
            heading = "My projects";
        } else if ("pending".equals(req.getParameter("view"))) {
            list = projects.findByStatuses(Project.PENDING, Project.REVISION);
            heading = "Pending review";
        } else {
            list = projects.findAll();
            heading = "All projects";
        }
        req.setAttribute("projects", list);
        req.setAttribute("heading", heading);
        forward(req, resp, "projects.jsp");
    }

    private void view(HttpServletRequest req, HttpServletResponse resp, User u) throws ServletException, IOException {
        Project p = projects.findById(Web.intParam(req, "id"));
        if (p == null) { resp.sendError(404, "Project not found"); return; }
        boolean owner = p.getOwnerId() == u.getId();
        if (!owner && !u.isFaculty()) { resp.sendError(403); return; }
        req.setAttribute("p", p);
        req.setAttribute("canEdit", owner && !Project.REJECTED.equals(p.getStatus()));
        forward(req, resp, "detail.jsp");
    }

    // ---------------- POST ----------------

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = Web.currentUser(req);
        String action = req.getParameter("action");
        if (action == null) { resp.sendError(400); return; }

        if ("create".equals(action)) { create(req, resp, u); return; }

        // Every other action works on an existing project owned by the signed-in student.
        Project p = projects.findById(Web.intParam(req, "id"));
        if (p == null) { resp.sendError(404, "Project not found"); return; }
        if (p.getOwnerId() != u.getId()) { resp.sendError(403); return; }
        int id = p.getId();
        boolean editable = !Project.REJECTED.equals(p.getStatus());

        switch (action) {
            case "delete":
                projects.delete(id);
                Web.flash(req, "Project deleted.");
                resp.sendRedirect(req.getContextPath() + "/project");
                return;
            case "resubmit":
                if (Project.REVISION.equals(p.getStatus())) {
                    projects.updateStatus(id, Project.PENDING);
                    Web.flash(req, "Proposal resubmitted for review.");
                }
                break;
            case "addMilestone":
                String title = Web.param(req, "title", 200);
                String due = Web.param(req, "due", 10);
                if (editable && !title.isEmpty()) {
                    try {
                        projects.addMilestone(id, title, due);
                    } catch (IllegalArgumentException e) { // bad date format
                        Web.flash(req, "Invalid due date.");
                    }
                }
                break;
            case "toggleMilestone":
                if (editable) projects.toggleMilestone(id, Web.intParam(req, "mid"));
                break;
            case "deleteMilestone":
                if (editable) projects.deleteMilestone(id, Web.intParam(req, "mid"));
                break;
            case "addUpdate":
                String body = Web.param(req, "body", 1000);
                if (editable && !body.isEmpty()) projects.addUpdate(id, body);
                break;
            default:
                resp.sendError(400);
                return;
        }
        resp.sendRedirect(req.getContextPath() + "/project?action=view&id=" + id);
    }

    private void create(HttpServletRequest req, HttpServletResponse resp, User u) throws ServletException, IOException {
        if (!u.isStudent()) { resp.sendError(403); return; }
        String title = Web.param(req, "title", 200);
        String description = Web.param(req, "description", 2000);
        if (title.isEmpty() || description.isEmpty()) {
            req.setAttribute("error", "Title and description are required.");
            forward(req, resp, "form.jsp");
            return;
        }
        Project p = new Project();
        p.setOwnerId(u.getId());
        p.setGroupName(Web.param(req, "group", 20));
        p.setTitle(title);
        p.setDescription(description);
        p.setTech(Web.param(req, "tech", 200));
        p.setTeam(Web.param(req, "team", 300));
        int id = projects.create(p);
        Web.flash(req, "Proposal submitted. Waiting for faculty review.");
        resp.sendRedirect(req.getContextPath() + "/project?action=view&id=" + id);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view).forward(req, resp);
    }
}
