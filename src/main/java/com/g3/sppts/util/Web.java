package com.g3.sppts.util;

import com.g3.sppts.model.User;
import javax.servlet.http.HttpServletRequest;

/** Small request helpers shared by the servlets. */
public final class Web {
    private Web() { }

    /** Returns the trimmed parameter, cut to max characters ("" when missing). */
    public static String param(HttpServletRequest req, String name, int max) {
        String v = req.getParameter(name);
        if (v == null) return "";
        v = v.trim();
        return v.length() > max ? v.substring(0, max) : v;
    }

    /** Returns the integer parameter or -1 if it is missing or not a number. */
    public static int intParam(HttpServletRequest req, String name) {
        try {
            return Integer.parseInt(req.getParameter(name));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static User currentUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("user");
    }

    /** One-time message shown on the next page (read and removed by the header JSP). */
    public static void flash(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flash", message);
    }
}
