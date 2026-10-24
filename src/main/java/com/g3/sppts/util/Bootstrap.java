package com.g3.sppts.util;

import com.g3.sppts.dao.ProjectDAO;
import com.g3.sppts.dao.UserDAO;
import com.g3.sppts.model.Project;
import com.g3.sppts.model.User;

/**
 * Prepares the database: creates the tables and loads demo data on the very first run.
 * Used by both the Swing application (App) and the web application (AppInitListener).
 */
public final class Bootstrap {
    private static boolean done;

    private Bootstrap() { }

    public static synchronized void init() {
        if (done) return;
        DBUtil.initSchema();
        UserDAO users = new UserDAO();
        if (users.count() == 0) seed(users, new ProjectDAO());
        done = true;
    }

    private static void seed(UserDAO users, ProjectDAO projects) {
        User faculty = users.create("Dr. Faculty Mentor", "faculty@college.edu", "faculty123", "faculty");
        User prashant = users.create("Prashant Kumar", "prashant@college.edu", "student123", "student");
        users.create("Uditansh Pant", "uditansh@college.edu", "student123", "student");

        Project p = new Project();
        p.setOwnerId(prashant.getId());
        p.setGroupName("G3");
        p.setTitle("Student Project Proposal and Progress Tracking System");
        p.setDescription("A platform where students submit project proposals, faculty review them, "
                + "and progress is tracked through milestones and updates.");
        p.setTech("Java Swing, Servlets, JSP, JDBC");
        p.setTeam("Prashant Kumar, Uditansh Pant");
        int id = projects.create(p);

        projects.review(id, Project.APPROVED, faculty.getName(), "Good scope. Proceed with implementation.");
        projects.addMilestone(id, "Requirement analysis", null);
        projects.addMilestone(id, "Database and UI design", null);
        projects.addMilestone(id, "Implementation", null);
        projects.addMilestone(id, "Testing", null);
        projects.addMilestone(id, "Final report and presentation", null);
        Project saved = projects.findById(id);
        projects.toggleMilestone(id, saved.getMilestones().get(0).getId());
        projects.toggleMilestone(id, saved.getMilestones().get(1).getId());
        projects.addUpdate(id, "Completed requirement analysis and finalised the design.");
    }
}
