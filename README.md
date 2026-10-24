# Student Project Proposal and Progress Tracking System

**Group G3 - Prashant Kumar, Uditansh Pant**

A Java web application where students submit project proposals, faculty review them,
and progress is tracked with milestones, updates and feedback.

## Advanced Java concepts used

| Concept | Where |
|---|---|
| Servlets (`HttpServlet`, `@WebServlet`) | `servlet/AuthServlet`, `DashboardServlet`, `ProjectServlet`, `ReviewServlet` |
| JSP + JSTL + EL | `webapp/WEB-INF/views/*.jsp` |
| JSP custom tag file | `WEB-INF/tags/projectCard.tag` |
| JSP include fragments | `_header.jspf`, `_footer.jspf` |
| MVC pattern | Servlets = controllers, JSPs = views, `model/` = models |
| JDBC (`DriverManager`, `PreparedStatement`, `ResultSet`) | `util/DBUtil`, `dao/*` |
| DAO pattern | `dao/UserDAO`, `dao/ProjectDAO` |
| JDBC transactions (commit / rollback) | `ProjectDAO.review()` + `DBUtil.inTransaction()` |
| Servlet Filters | `filter/AuthFilter` (login check), `filter/EncodingFilter` (UTF-8) |
| Listener (`ServletContextListener`) | `listener/AppInitListener` (creates tables, seeds demo data) |
| HTTP sessions, session timeout, session-fixation protection | `AuthServlet`, `web.xml` |
| Post/Redirect/Get and flash messages | `ProjectServlet`, `Web.flash()` |
| Error pages in `web.xml` | `views/error.jsp` |
| Password hashing (PBKDF2 + salt) | `util/PasswordUtil` |
| Lambdas / functional interface for JDBC work | `DBUtil.SqlWork` |
| Security: SQL-injection safe queries, XSS-safe output (`<c:out>`), role checks | all servlets / JSPs |

## Run it

Requirements: JDK 11 or newer and Maven.

```bash
mvn jetty:run
```

Open <http://localhost:8080/sppts/>

To deploy on your own **Tomcat 9** instead: `mvn package`, then copy `target/sppts.war`
into Tomcat's `webapps/` folder. (Tomcat 10+ uses the `jakarta.*` namespace and needs the
code migrated.)

Demo logins:

| Role | Email | Password |
|---|---|---|
| Student | prashant@college.edu | student123 |
| Student | uditansh@college.edu | student123 |
| Faculty | faculty@college.edu | faculty123 |

## Database

By default it uses an embedded **H2** database stored in `./data/` (no setup needed).
To use **MySQL**, edit `src/main/resources/db.properties` (instructions are inside).
Tables are created automatically on startup from `src/main/resources/schema.sql`.

## Features

- Register / sign in as student or faculty
- Students: submit proposals, add milestones with due dates, tick them off, post progress updates, resubmit after revision, delete projects
- Faculty: see all and pending projects, approve / reject / request revision / mark completed, leave feedback
- Dashboard with counts and average progress

## Hosting note

GitHub Pages only serves static files and **cannot run Java**. Put this source code on GitHub
for submission, and use a Java-capable host (Render, Railway, etc.) with the included
`Dockerfile` if you need a live link.
