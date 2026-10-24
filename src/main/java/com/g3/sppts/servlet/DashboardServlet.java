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

/** Summary page: counts, average progress and the five most recent projects. */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private final ProjectDAO projects = new ProjectDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = Web.currentUser(req);
        List<Project> list = u.isFaculty() ? projects.findAll() : projects.findByOwner(u.getId());

        req.setAttribute("total", list.size());
        req.setAttribute("pending", count(list, Project.PENDING));
        req.setAttribute("approved", count(list, Project.APPROVED));
        req.setAttribute("revision", count(list, Project.REVISION));
        req.setAttribute("avg", (int) Math.round(list.stream().mapToInt(Project::getProgress).average().orElse(0)));
        req.setAttribute("recent", list.subList(0, Math.min(5, list.size())));
        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }

    private static long count(List<Project> list, String status) {
        return list.stream().filter(p -> status.equals(p.getStatus())).count();
    }
}
