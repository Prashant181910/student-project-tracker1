package com.g3.sppts.listener;

import com.g3.sppts.util.Bootstrap;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/** Runs once when the web application starts: creates the tables and loads demo data on first run. */
@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Bootstrap.init();
        sce.getServletContext().log("SPPTS database ready");
    }
}
