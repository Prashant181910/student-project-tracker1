package com.g3.sppts.servlet;

import com.g3.sppts.dao.UserDAO;
import com.g3.sppts.model.User;
import com.g3.sppts.util.Web;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Login, registration and logout. */
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    private final UserDAO users = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession s = req.getSession(false);
            if (s != null) s.invalidate();
            resp.sendRedirect(req.getContextPath() + "/auth");
            return;
        }
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        String view = "register".equals(action) ? "register.jsp" : "login.jsp";
        req.getRequestDispatcher("/WEB-INF/views/" + view).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("register".equals(req.getParameter("action"))) register(req, resp);
        else login(req, resp);
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = Web.param(req, "email", 120).toLowerCase();
        User u = users.authenticate(email, req.getParameter("password"));
        if (u == null) {
            req.setAttribute("error", "Invalid email or password.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }
        signIn(req, resp, u);
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = Web.param(req, "name", 100);
        String email = Web.param(req, "email", 120).toLowerCase();
        String password = req.getParameter("password");
        String role = "faculty".equals(req.getParameter("role")) ? "faculty" : "student";

        String error = null;
        if (name.isEmpty() || email.isEmpty() || password == null || password.isEmpty()) error = "Fill in all fields.";
        else if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) error = "Enter a valid email address.";
        else if (password.length() < 6) error = "Password must be at least 6 characters.";
        else if (users.emailExists(email)) error = "That email is already registered.";

        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }
        signIn(req, resp, users.create(name, email, password, role));
    }

    /** Starts a fresh session (prevents session fixation) and goes to the dashboard. */
    private void signIn(HttpServletRequest req, HttpServletResponse resp, User u) throws IOException {
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate();
        req.getSession(true).setAttribute("user", u);
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
