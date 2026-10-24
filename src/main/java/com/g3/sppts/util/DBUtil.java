package com.g3.sppts.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/** JDBC helper: opens connections, runs work and transactions, creates the schema. */
public final class DBUtil {

    /** A unit of JDBC work that returns a result. */
    @FunctionalInterface
    public interface SqlWork<T> {
        T apply(Connection c) throws SQLException;
    }

    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new IllegalStateException("db.properties not found on classpath");
            CONFIG.load(in);
            String url = CONFIG.getProperty("db.url");
            // Register the driver explicitly (needed inside some servlet containers)
            if (url.startsWith("jdbc:h2:")) Class.forName("org.h2.Driver");
            else if (url.startsWith("jdbc:mysql:")) Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBUtil() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getProperty("db.url"),
                CONFIG.getProperty("db.user", ""),
                CONFIG.getProperty("db.password", ""));
    }

    /** Runs work on a fresh connection and closes it afterwards. */
    public static <T> T run(SqlWork<T> work) {
        try (Connection c = getConnection()) {
            return work.apply(c);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** Runs work in a single transaction: commit on success, rollback on any failure. */
    public static <T> T inTransaction(SqlWork<T> work) {
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try {
                T result = work.apply(c);
                c.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** Executes every statement in schema.sql (all use CREATE TABLE IF NOT EXISTS). */
    public static void initSchema() {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (in == null) throw new IllegalStateException("schema.sql not found on classpath");
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection c = getConnection(); Statement st = c.createStatement()) {
                for (String part : sql.split(";")) {
                    if (!part.trim().isEmpty()) st.execute(part);
                }
            }
        } catch (IOException | SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
