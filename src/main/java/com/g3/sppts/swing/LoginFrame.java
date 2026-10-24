package com.g3.sppts.swing;

import com.g3.sppts.dao.ProjectDAO;
import com.g3.sppts.dao.UserDAO;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Sign-in window with a "Create account" dialog. */
public class LoginFrame extends JFrame {
    private final UserDAO users;
    private final ProjectDAO projects;

    final JTextField emailField = new JTextField(22);
    final JPasswordField passwordField = new JPasswordField(22);
    final JButton loginButton = new JButton("Sign in");
    final JButton registerButton = new JButton("Create account");
    final JLabel message = new JLabel(" ");

    public LoginFrame(UserDAO users, ProjectDAO projects) {
        super("Student Project Proposal and Progress Tracking System");
        this.users = users;
        this.projects = projects;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.gridx = 0;
        g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = Ui.bold("Student Project Proposal and Progress Tracking System", 16f);
        title.setForeground(Ui.BRAND);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 0;
        root.add(title, g);

        JLabel group = new JLabel("Group G3 - Prashant Kumar & Uditansh Pant");
        group.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 1;
        g.insets = new Insets(0, 4, 18, 4);
        root.add(group, g);
        g.insets = new Insets(4, 4, 4, 4);

        g.gridwidth = 1;
        g.gridy = 2; g.gridx = 0; g.weightx = 0;
        root.add(new JLabel("Email:"), g);
        g.gridx = 1; g.weightx = 1;
        root.add(emailField, g);

        g.gridy = 3; g.gridx = 0; g.weightx = 0;
        root.add(new JLabel("Password:"), g);
        g.gridx = 1; g.weightx = 1;
        root.add(passwordField, g);

        message.setForeground(new Color(0xe03131));
        g.gridy = 4; g.gridx = 0; g.gridwidth = 2;
        root.add(message, g);

        JPanel buttons = new JPanel();
        buttons.add(loginButton);
        buttons.add(registerButton);
        g.gridy = 5;
        root.add(buttons, g);

        JLabel demo = new JLabel("<html><small>Demo: prashant@college.edu / student123<br>"
                + "Faculty: faculty@college.edu / faculty123</small></html>");
        demo.setForeground(Color.GRAY);
        g.gridy = 6;
        root.add(demo, g);

        setContentPane(root);
        getRootPane().setDefaultButton(loginButton); // Enter key signs in
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> showRegisterDialog());

        pack();
        setLocationRelativeTo(null);
    }

    private void login() {
        String email = emailField.getText().trim().toLowerCase();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            message.setText("Enter your email and password.");
            return;
        }
        loginButton.setEnabled(false);
        message.setText("Signing in...");
        Async.run(this, () -> users.authenticate(email, password), user -> {
            loginButton.setEnabled(true);
            if (user == null) {
                message.setText("Invalid email or password.");
                return;
            }
            new MainFrame(user, users, projects).setVisible(true);
            dispose();
        }, () -> {
            loginButton.setEnabled(true);
            message.setText(" ");
        });
    }

    private void showRegisterDialog() {
        JDialog d = new JDialog(this, "Create account", true);
        JTextField name = new JTextField(20);
        JTextField email = new JTextField(20);
        JPasswordField pass = new JPasswordField(20);
        JComboBox<String> role = new JComboBox<>(new String[] { "student", "faculty" });
        JLabel err = new JLabel(" ");
        err.setForeground(new Color(0xe03131));
        JButton ok = new JButton("Register");
        JButton cancel = new JButton("Cancel");

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        String[] labels = { "Full name:", "Email:", "Password (min 6):", "Role:" };
        java.awt.Component[] fields = { name, email, pass, role };
        for (int i = 0; i < labels.length; i++) {
            g.gridy = i; g.gridx = 0; g.weightx = 0;
            p.add(new JLabel(labels[i]), g);
            g.gridx = 1; g.weightx = 1;
            p.add(fields[i], g);
        }
        g.gridy = 4; g.gridx = 0; g.gridwidth = 2;
        p.add(err, g);
        JPanel bp = new JPanel();
        bp.add(ok);
        bp.add(cancel);
        g.gridy = 5;
        p.add(bp, g);

        ok.addActionListener(e -> {
            String n = name.getText().trim();
            String em = email.getText().trim().toLowerCase();
            String pw = new String(pass.getPassword());
            String problem = null;
            if (n.isEmpty() || em.isEmpty() || pw.isEmpty()) problem = "Fill in all fields.";
            else if (!em.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) problem = "Enter a valid email address.";
            else if (pw.length() < 6) problem = "Password must be at least 6 characters.";
            if (problem != null) {
                err.setText(problem);
                return;
            }
            ok.setEnabled(false);
            Async.run(d, () -> {
                if (users.emailExists(em)) return false;
                users.create(n, em, pw, (String) role.getSelectedItem());
                return true;
            }, created -> {
                ok.setEnabled(true);
                if (!created) {
                    err.setText("That email is already registered.");
                    return;
                }
                emailField.setText(em);
                passwordField.setText("");
                message.setText("Account created. Please sign in.");
                d.dispose();
            }, () -> ok.setEnabled(true));
        });
        cancel.addActionListener(e -> d.dispose());

        d.setContentPane(p);
        d.getRootPane().setDefaultButton(ok);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
}
