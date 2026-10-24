package com.g3.sppts.swing;

import com.g3.sppts.dao.ProjectDAO;
import com.g3.sppts.model.Project;
import com.g3.sppts.model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/** Summary cards (counts and average progress) plus the most recent projects. */
public class DashboardPanel extends JPanel {
    private final User user;
    private final ProjectDAO dao;

    final JLabel total = new JLabel("0");
    final JLabel pending = new JLabel("0");
    final JLabel approved = new JLabel("0");
    final JLabel revision = new JLabel("0");
    final JLabel average = new JLabel("0%");
    final JProgressBar averageBar = new JProgressBar(0, 100);
    final DefaultListModel<String> recent = new DefaultListModel<>();

    public DashboardPanel(User user, ProjectDAO dao) {
        this.user = user;
        this.dao = dao;
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel cards = new JPanel(new GridLayout(1, 5, 12, 0));
        cards.add(card("Total projects", total));
        cards.add(card("Pending review", pending));
        cards.add(card("Approved", approved));
        cards.add(card("Needs revision", revision));
        cards.add(card("Average progress", average));
        add(cards, BorderLayout.NORTH);

        JPanel middle = new JPanel(new BorderLayout(0, 6));
        averageBar.setStringPainted(true);
        middle.add(Ui.section("Overall progress", averageBar), BorderLayout.NORTH);
        JList<String> list = new JList<>(recent);
        middle.add(Ui.section("Recent projects", new JScrollPane(list)), BorderLayout.CENTER);
        add(middle, BorderLayout.CENTER);
    }

    private static JPanel card(String caption, JLabel value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xe4e8f0)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        value.setFont(value.getFont().deriveFont(java.awt.Font.BOLD, 28f));
        JLabel cap = new JLabel(caption);
        cap.setForeground(Color.GRAY);
        cap.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(value, BorderLayout.CENTER);
        p.add(cap, BorderLayout.SOUTH);
        return p;
    }

    /** Loads the numbers on a background thread, then updates the labels. */
    public void refresh() {
        Async.run(this,
                () -> user.isFaculty() ? dao.findAll() : dao.findByOwner(user.getId()),
                this::show);
    }

    private void show(List<Project> list) {
        total.setText(String.valueOf(list.size()));
        pending.setText(String.valueOf(count(list, Project.PENDING)));
        approved.setText(String.valueOf(count(list, Project.APPROVED)));
        revision.setText(String.valueOf(count(list, Project.REVISION)));
        int avg = (int) Math.round(list.stream().mapToInt(Project::getProgress).average().orElse(0));
        average.setText(avg + "%");
        averageBar.setValue(avg);
        recent.clear();
        for (int i = 0; i < Math.min(5, list.size()); i++) {
            Project p = list.get(i);
            recent.addElement(p.getTitle() + "   [" + p.getStatusLabel() + ", " + p.getProgress() + "%]");
        }
    }

    private static long count(List<Project> list, String status) {
        return list.stream().filter(p -> status.equals(p.getStatus())).count();
    }
}
