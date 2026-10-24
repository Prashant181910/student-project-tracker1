package com.g3.sppts.swing;

import com.g3.sppts.dao.ProjectDAO;
import com.g3.sppts.model.Entry;
import com.g3.sppts.model.Milestone;
import com.g3.sppts.model.Project;
import com.g3.sppts.model.User;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

/** Shows one project: details, milestones, updates, feedback, and the student/faculty actions. */
public class ProjectDetailPanel extends JPanel {
    private final User user;
    private final ProjectDAO dao;
    private final Runnable onChanged;
    private Project project;

    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);

    final JLabel titleLabel = Ui.bold(" ", 15f);
    final JLabel metaLabel = new JLabel(" ");
    final JLabel metaLabel2 = new JLabel(" ");
    final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    final JTextArea descriptionArea = new JTextArea(4, 30);
    final JProgressBar progressBar = new JProgressBar(0, 100);

    private final MilestoneTableModel milestoneModel = new MilestoneTableModel();
    final JTable milestoneTable = new JTable(milestoneModel);
    final JTextField milestoneTitle = new JTextField(16);
    final JTextField milestoneDue = new JTextField(8);
    final JButton addMilestone = new JButton("Add");
    final JButton deleteMilestone = new JButton("Delete selected");

    final DefaultListModel<String> updatesModel = new DefaultListModel<>();
    final JTextField updateText = new JTextField(22);
    final JButton postUpdate = new JButton("Post update");

    final DefaultListModel<String> feedbackModel = new DefaultListModel<>();

    final JTextField reviewComment = new JTextField(24);
    final JButton approveButton = new JButton("Approve");
    final JButton reviseButton = new JButton("Request revision");
    final JButton rejectButton = new JButton("Reject");
    final JButton completeButton = new JButton("Mark completed");

    final JButton resubmitButton = new JButton("Resubmit for review");
    final JButton deleteButton = new JButton("Delete project");

    public ProjectDetailPanel(User user, ProjectDAO dao, Runnable onChanged) {
        this.user = user;
        this.dao = dao;
        this.onChanged = onChanged;
        setLayout(new BorderLayout());

        JLabel empty = new JLabel("Select a project from the list", SwingConstants.CENTER);
        empty.setForeground(Color.GRAY);
        cardPanel.add(empty, "empty");
        cardPanel.add(new JScrollPane(buildContent()), "detail");
        add(cardPanel, BorderLayout.CENTER);
        clear();
    }

    private JPanel buildContent() {
        JPanel content = new WidthPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // ----- project info -----
        statusLabel.setOpaque(true);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        statusWrap.add(statusLabel);
        JPanel metaRow1 = new JPanel(new BorderLayout());
        metaRow1.add(metaLabel, BorderLayout.CENTER);
        metaRow1.add(statusWrap, BorderLayout.EAST);
        JPanel meta = new JPanel(new GridLayout(2, 1));
        meta.add(metaRow1);
        meta.add(metaLabel2);
        JPanel head = new JPanel(new BorderLayout(0, 2));
        head.add(titleLabel, BorderLayout.NORTH);
        head.add(meta, BorderLayout.CENTER);

        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setOpaque(false);
        progressBar.setStringPainted(true);
        JPanel info = new JPanel(new BorderLayout(0, 6));
        info.add(head, BorderLayout.NORTH);
        info.add(descriptionArea, BorderLayout.CENTER);
        info.add(progressBar, BorderLayout.SOUTH);
        JPanel infoSection = Ui.section("Project", info);
        content.add(infoSection);

        // ----- faculty review -----
        if (user.isFaculty()) {
            JPanel rvRow1 = new JPanel(new BorderLayout(4, 0));
            rvRow1.add(new JLabel("Comment:"), BorderLayout.WEST);
            rvRow1.add(reviewComment, BorderLayout.CENTER);
            JPanel rvRow2 = new JPanel(new GridLayout(1, 4, 4, 0));
            rvRow2.add(approveButton);
            rvRow2.add(reviseButton);
            rvRow2.add(rejectButton);
            rvRow2.add(completeButton);
            JPanel rv = new JPanel(new GridLayout(2, 1, 0, 4));
            rv.add(rvRow1);
            rv.add(rvRow2);
            content.add(Ui.section("Faculty review", rv));
        }

        // ----- student actions -----
        if (user.isStudent()) {
            JPanel ac = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
            ac.add(resubmitButton);
            ac.add(deleteButton);
            content.add(Ui.section("Actions", ac));
        }

        // ----- milestones -----
        milestoneTable.setRowHeight(22);
        milestoneTable.getColumnModel().getColumn(0).setMaxWidth(60);
        milestoneTable.getColumnModel().getColumn(2).setMaxWidth(110);
        JScrollPane msScroll = new JScrollPane(milestoneTable);
        msScroll.setPreferredSize(new Dimension(300, 115));
        JPanel msRow1 = new JPanel(new BorderLayout(4, 0));
        msRow1.add(new JLabel("Title:"), BorderLayout.WEST);
        msRow1.add(milestoneTitle, BorderLayout.CENTER);
        JPanel dueBox = new JPanel(new BorderLayout(4, 0));
        dueBox.add(new JLabel("Due (yyyy-mm-dd):"), BorderLayout.WEST);
        dueBox.add(milestoneDue, BorderLayout.CENTER);
        msRow1.add(dueBox, BorderLayout.EAST);
        JPanel msRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        msRow2.add(addMilestone);
        msRow2.add(deleteMilestone);
        JPanel msAdd = new JPanel(new GridLayout(2, 1, 0, 2));
        msAdd.add(msRow1);
        msAdd.add(msRow2);
        JPanel msBody = new JPanel(new BorderLayout());
        msBody.add(msScroll, BorderLayout.CENTER);
        msBody.add(msAdd, BorderLayout.SOUTH);
        content.add(Ui.section("Milestones (tick the box when done)", msBody));

        // ----- updates -----
        JScrollPane upScroll = new JScrollPane(new JList<>(updatesModel));
        upScroll.setPreferredSize(new Dimension(300, 65));
        JPanel upAdd = new JPanel(new BorderLayout(4, 0));
        upAdd.add(updateText, BorderLayout.CENTER);
        upAdd.add(postUpdate, BorderLayout.EAST);
        JPanel upBody = new JPanel(new BorderLayout());
        upBody.add(upScroll, BorderLayout.CENTER);
        upBody.add(upAdd, BorderLayout.SOUTH);
        content.add(Ui.section("Progress updates", upBody));

        // ----- feedback -----
        JScrollPane fbScroll = new JScrollPane(new JList<>(feedbackModel));
        fbScroll.setPreferredSize(new Dimension(300, 65));
        content.add(Ui.section("Faculty feedback", fbScroll));

        for (java.awt.Component c : content.getComponents()) Ui.fixHeight((javax.swing.JComponent) c);

        // ----- event handlers -----
        addMilestone.addActionListener(e -> doAddMilestone());
        deleteMilestone.addActionListener(e -> doDeleteMilestone());
        postUpdate.addActionListener(e -> doPostUpdate());
        approveButton.addActionListener(e -> doReview(Project.APPROVED, false));
        reviseButton.addActionListener(e -> doReview(Project.REVISION, false));
        rejectButton.addActionListener(e -> doReview(Project.REJECTED, true));
        completeButton.addActionListener(e -> doReview(Project.COMPLETED, false));
        resubmitButton.addActionListener(e -> doResubmit());
        deleteButton.addActionListener(e -> doDelete());
        milestoneModel.onToggle = this::doToggleMilestone;
        return content;
    }

    // ---------------------------------------------------------------- show / load

    public void clear() {
        project = null;
        cards.show(cardPanel, "empty");
    }

    /** Loads the project (with updates and feedback) on a background thread, then shows it. */
    public void load(int projectId) {
        Async.run(this, () -> dao.findById(projectId), p -> {
            if (p == null) clear();
            else show(p);
        });
    }

    private void show(Project p) {
        this.project = p;
        boolean owner = p.getOwnerId() == user.getId();
        boolean canEdit = owner && !Project.REJECTED.equals(p.getStatus());

        titleLabel.setText(p.getTitle());
        titleLabel.setToolTipText(p.getTitle());
        metaLabel.setText("Group " + (p.getGroupName() == null || p.getGroupName().isEmpty() ? "-" : p.getGroupName())
                + "  |  " + p.getTeam() + "  |  by " + p.getOwnerName());
        metaLabel2.setText("Submitted " + p.getSubmitted()
                + "  |  Technologies: " + (p.getTech() == null || p.getTech().isEmpty() ? "-" : p.getTech()));
        statusLabel.setText(p.getStatusLabel());
        statusLabel.setBackground(Ui.statusColor(p.getStatus()));
        descriptionArea.setText(p.getDescription());
        progressBar.setValue(p.getProgress());

        milestoneModel.setData(p.getMilestones(), canEdit);
        for (JButton b : new JButton[] { addMilestone, deleteMilestone, postUpdate }) b.setEnabled(canEdit);
        milestoneTitle.setEnabled(canEdit);
        milestoneDue.setEnabled(canEdit);
        updateText.setEnabled(canEdit);

        updatesModel.clear();
        for (Entry e : p.getUpdates()) updatesModel.addElement(e.getDate() + "  -  " + e.getBody());
        feedbackModel.clear();
        for (Entry e : p.getFeedback()) feedbackModel.addElement(e.getDate() + "  (" + e.getAuthor() + ")  -  " + e.getBody());

        completeButton.setEnabled(Project.APPROVED.equals(p.getStatus()));
        resubmitButton.setEnabled(owner && Project.REVISION.equals(p.getStatus()));
        deleteButton.setEnabled(owner);

        cards.show(cardPanel, "detail");
        revalidate();
    }

    /** Reloads this project and tells the other tabs that data changed. */
    private void reloadAndNotify() {
        if (project != null) load(project.getId());
        onChanged.run();
    }

    // ---------------------------------------------------------------- actions

    private void doAddMilestone() {
        String title = milestoneTitle.getText().trim();
        String due = milestoneDue.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a milestone title.", "Missing title", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!due.isEmpty()) {
            try {
                LocalDate.parse(due);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Due date must look like 2026-12-31.", "Invalid date",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        final int id = project.getId();
        Async.run(this, () -> {
            dao.addMilestone(id, title, due);
            return true;
        }, ok -> {
            milestoneTitle.setText("");
            milestoneDue.setText("");
            reloadAndNotify();
        });
    }

    private void doDeleteMilestone() {
        int row = milestoneTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a milestone first.", "Nothing selected",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        final int pid = project.getId();
        final int mid = milestoneModel.get(row).getId();
        Async.run(this, () -> {
            dao.deleteMilestone(pid, mid);
            return true;
        }, ok -> reloadAndNotify());
    }

    private void doToggleMilestone(Milestone m) {
        final int pid = project.getId();
        Async.run(this, () -> {
            dao.toggleMilestone(pid, m.getId());
            return true;
        }, ok -> reloadAndNotify());
    }

    private void doPostUpdate() {
        String text = updateText.getText().trim();
        if (text.isEmpty()) return;
        final int id = project.getId();
        Async.run(this, () -> {
            dao.addUpdate(id, text);
            return true;
        }, ok -> {
            updateText.setText("");
            reloadAndNotify();
        });
    }

    private void doReview(String status, boolean confirm) {
        if (confirm && JOptionPane.showConfirmDialog(this, "Reject this project?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        final int id = project.getId();
        final String comment = reviewComment.getText().trim();
        Async.run(this, () -> {
            dao.review(id, status, user.getName(), comment); // one JDBC transaction
            return true;
        }, ok -> {
            reviewComment.setText("");
            reloadAndNotify();
        });
    }

    private void doResubmit() {
        final int id = project.getId();
        Async.run(this, () -> {
            dao.updateStatus(id, Project.PENDING);
            return true;
        }, ok -> reloadAndNotify());
    }

    private void doDelete() {
        if (JOptionPane.showConfirmDialog(this, "Delete this project permanently?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        final int id = project.getId();
        Async.run(this, () -> {
            dao.delete(id);
            return true;
        }, ok -> {
            clear();
            onChanged.run();
        });
    }

    /** Content panel that always matches the scroll pane's width (so nothing is cut off sideways). */
    static class WidthPanel extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) { return 80; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    // ---------------------------------------------------------------- milestone table model

    /** Milestones table: a checkbox column (editable by the owner), the title and the due date. */
    static class MilestoneTableModel extends AbstractTableModel {
        private final String[] cols = { "Done", "Milestone", "Due" };
        private List<Milestone> rows = new ArrayList<>();
        private boolean editable;
        java.util.function.Consumer<Milestone> onToggle = m -> { };

        void setData(List<Milestone> list, boolean editable) {
            this.rows = new ArrayList<>(list);
            this.editable = editable;
            fireTableDataChanged();
        }

        Milestone get(int row) { return rows.get(row); }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }
        @Override public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : String.class; }
        @Override public boolean isCellEditable(int r, int c) { return editable && c == 0; }

        @Override
        public Object getValueAt(int r, int c) {
            Milestone m = rows.get(r);
            switch (c) {
                case 0: return m.isDone();
                case 1: return m.getTitle();
                default: return m.getDueDate() == null ? "" : m.getDueDate();
            }
        }

        @Override
        public void setValueAt(Object value, int r, int c) {
            if (c == 0) onToggle.accept(rows.get(r)); // saved to the database, then the list is reloaded
        }
    }
}
