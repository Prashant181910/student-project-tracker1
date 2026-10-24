package com.g3.sppts.swing;

import com.g3.sppts.dao.ProjectDAO;
import com.g3.sppts.model.Project;
import com.g3.sppts.model.User;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

/** List of projects (left) and the selected project's details (right). */
public class ProjectsPanel extends JPanel {
    private final User user;
    private final ProjectDAO dao;

    final ProjectTableModel model = new ProjectTableModel();
    final JTable table = new JTable(model);
    ProjectDetailPanel detail;
    final JComboBox<String> filter = new JComboBox<>(new String[] { "All projects", "Pending review" });
    final JButton refreshButton = new JButton("Refresh");

    public ProjectsPanel(User user, ProjectDAO dao, Runnable onDataChanged) {
        this.user = user;
        this.dao = dao;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (user.isFaculty()) {
            top.add(new JLabel("Show:"));
            top.add(filter);
            filter.addActionListener(e -> refresh());
        }
        top.add(refreshButton);
        refreshButton.addActionListener(e -> refresh());
        add(top, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(3).setCellRenderer(new Ui.StatusRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new Ui.ProgressRenderer());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] widths = { 165, 55, 100, 95, 75 };
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                detail.clear();
            } else {
                int row = table.convertRowIndexToModel(viewRow); // sorting changes the view order
                detail.load(model.getProject(row).getId());
            }
        });

        detail = new ProjectDetailPanel(user, dao, onDataChanged);
        JScrollPane left = new JScrollPane(table);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, detail);
        split.setResizeWeight(0.45);
        split.setDividerLocation(470);
        add(split, BorderLayout.CENTER);
    }

    /** Reloads the list on a background thread and keeps the same project selected. */
    public void refresh() {
        final int selectedId = selectedProjectId();
        final boolean pendingOnly = user.isFaculty() && filter.getSelectedIndex() == 1;
        Async.run(this, () -> {
            if (user.isStudent()) return dao.findByOwner(user.getId());
            return pendingOnly ? dao.findByStatuses(Project.PENDING, Project.REVISION) : dao.findAll();
        }, list -> {
            model.setProjects(list);
            int row = model.indexOfId(selectedId);
            if (row >= 0) {
                int view = table.convertRowIndexToView(row);
                table.getSelectionModel().setSelectionInterval(view, view);
            } else {
                detail.clear();
            }
        });
    }

    private int selectedProjectId() {
        int viewRow = table.getSelectedRow();
        return viewRow < 0 ? -1 : model.getProject(table.convertRowIndexToModel(viewRow)).getId();
    }
}
