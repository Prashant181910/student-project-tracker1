package com.g3.sppts.dao;

import com.g3.sppts.model.User;
import com.g3.sppts.util.DBUtil;
import com.g3.sppts.util.PasswordUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Data access for the users table (all queries use PreparedStatement). */
public class UserDAO {

    public User authenticate(String email, String password) {
        return DBUtil.run(c -> {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT id, name, email, password_hash, role FROM users WHERE email = ?")) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && PasswordUtil.verify(password, rs.getString("password_hash"))) {
                        return new User(rs.getInt("id"), rs.getString("name"),
                                rs.getString("email"), rs.getString("role"));
                    }
                    return null;
                }
            }
        });
    }

    public boolean emailExists(String email) {
        return DBUtil.run(c -> {
            try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM users WHERE email = ?")) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    public User create(String name, String email, String password, String role) {
        return DBUtil.run(c -> {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, PasswordUtil.hash(password));
                ps.setString(4, role);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    return new User(keys.getInt(1), name, email, role);
                }
            }
        });
    }

    public int count() {
        return DBUtil.run(c -> {
            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM users");
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        });
    }
}
