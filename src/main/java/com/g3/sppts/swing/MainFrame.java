package com.g3.sppts.swing;

import com.g3.sppts.dao.ProjectDAO;
import com.g3.sppts.dao.UserDAO;
import com.g3.sppts.model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/** Main window shown after sign-in. Students and faculty see different tabs. */
public class MainFrame extends JFrame {
    private final User user;
    private final UserDAO users;
    private final ProjectDAO projects;

    final JTabbedPane tabs = new JTabbedPane();
    final DashboardPanel dashboard;
    final ProjectsPanel projectsPanel;
    final ProposalPanel proposalPanel; // null for faculty

    public MainFrame(User user, UserDAO users, ProjectDAO projects) {
        super("Student Project Proposal and Progress Tracking System");
        this.user = user;
        this.users = users;
        this.projects = projects;
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ----- header -----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Ui.BRAND);
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel title = Ui.bold("Student Project Proposal and Progress Tracking System", 15f);
        title.setForeground(Color.WHITE);
        JLabel who = new JLabel(user.getName() + " (" + user.getRole() + ")");
        who.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        header.add(who, BorderLayout.EAST);

        // ----- tabs -----
        dashboard = new DashboardPanel(user, projects);
        projectsPanel = new ProjectsPanel(user, projects, this::dataChanged);
        tabs.addTab("Dashboard", dashboard);
        if (user.isStudent()) {
            proposalPanel = new ProposalPanel(user, projects, () -> {
                dataChanged();
                tabs.setSelectedComponent(projectsPanel);
            });
            tabs.addTab("New Proposal", proposalPanel);
            tabs.addTab("My Projects", projectsPanel);
        } else {
            proposalPanel = null;
            tabs.addTab("All Projects", projectsPanel);
        }
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == dashboard) dashboard.refresh();
        });

        setJMenuBar(buildMenu());
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        setMinimumSize(new Dimension(950, 620));
        setSize(1150, 720);
        setLocationRelativeTo(null);

        dashboard.refresh();
        projectsPanel.refresh();
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenuItem logout = new JMenuItem("Sign out");
        JMenuItem exit = new JMenuItem("Exit");
        logout.addActionListener(e -> {
            new LoginFrame(users, projects).setVisible(true);
            dispose();
        });
        exit.addActionListener(e -> System.exit(0));
        file.add(logout);
        file.addSeparator();
        file.add(exit);

        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Student Project Proposal and Progress Tracking System\n"
                        + "Group G3: Prashant Kumar, Uditansh Pant\n"
                        + "Built with Java Swing, JDBC and the DAO pattern.",
                "About", JOptionPane.INFORMATION_MESSAGE));
        help.add(about);

        bar.add(file);
        bar.add(help);
        return bar;
    }

    /** Called after any change so every tab shows fresh data. */
    private void dataChanged() {
        dashboard.refresh();
        projectsPanel.refresh();
    }
}
