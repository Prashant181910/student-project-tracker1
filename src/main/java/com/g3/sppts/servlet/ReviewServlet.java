package com.g3.sppts.servlet;

import com.g3.sppts.dao.ProjectDAO;
import com.g3.sppts.model.Project;
import com.g3.sppts.model.User;
import com.g3.sppts.util.Web;
import java.io.IOException;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Faculty-only: approve, reject, request revision or complete a project. */
@WebServlet("/review")
public class ReviewServlet extends HttpServlet {
    private static final Set<String> ALLOWED = Set.of(
            Project.APPROVED, Project.REVISION, Project.REJECTED, Project.COMPLETED);
    private final ProjectDAO projects = new ProjectDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = Web.currentUser(req);
        if (!u.isFaculty()) { resp.sendError(403); return; }

        int id = Web.intParam(req, "id");
        String status = req.getParameter("status");
        if (status == null || !ALLOWED.contains(status)) { resp.sendError(400); return; }
        if (projects.findById(id) == null) { resp.sendError(404); return; }

        projects.review(id, status, u.getName(), Web.param(req, "comment", 1000));
        Web.flash(req, "Project marked as " + status + ".");
        resp.sendRedirect(req.getContextPath() + "/project?action=view&id=" + id);
    }
}
