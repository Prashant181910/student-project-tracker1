package com.g3.sppts.swing;

import com.g3.sppts.dao.ProjectDAO;
import com.g3.sppts.model.Project;
import com.g3.sppts.model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Form for a student to submit a new project proposal. */
public class ProposalPanel extends JPanel {
    private final User user;
    private final ProjectDAO dao;
    private final Runnable onCreated;

    final JTextField group = new JTextField("G3", 8);
    final JTextField team = new JTextField(30);
    final JTextField title = new JTextField(30);
    final JTextField tech = new JTextField(30);
    final JTextArea description = new JTextArea(7, 30);
    final JLabel error = new JLabel(" ");
    final JButton submit = new JButton("Submit proposal");
    final JButton clear = new JButton("Clear");

    public ProposalPanel(User user, ProjectDAO dao, Runnable onCreated) {
        this.user = user;
        this.dao = dao;
        this.onCreated = onCreated;
        team.setText(user.getName());
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        error.setForeground(new Color(0xe03131));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Submit a project proposal"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.NORTHWEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        addRow(form, g, 0, "Group:", group);
        addRow(form, g, 1, "Team members:", team);
        addRow(form, g, 2, "Project title *:", title);
        addRow(form, g, 3, "Technologies:", tech);
        g.gridy = 4; g.gridx = 0; g.weightx = 0;
        form.add(new JLabel("Description *:"), g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.BOTH; g.weighty = 1;
        form.add(new JScrollPane(description), g);
        g.weighty = 0; g.fill = GridBagConstraints.HORIZONTAL;

        g.gridy = 5; g.gridx = 1;
        form.add(error, g);
        JPanel buttons = new JPanel();
        buttons.add(submit);
        buttons.add(clear);
        g.gridy = 6;
        form.add(buttons, g);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(form, BorderLayout.CENTER);

        submit.addActionListener(e -> submitProposal());
        clear.addActionListener(e -> reset());

        // hide the red error message as soon as the user starts typing again
        DocumentListener hideError = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { error.setText(" "); }
            @Override public void removeUpdate(DocumentEvent e) { error.setText(" "); }
            @Override public void changedUpdate(DocumentEvent e) { error.setText(" "); }
        };
        title.getDocument().addDocumentListener(hideError);
        description.getDocument().addDocumentListener(hideError);
    }

    private static void addRow(JPanel p, GridBagConstraints g, int row, String label, JTextField field) {
        g.gridy = row; g.gridx = 0; g.weightx = 0;
        p.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 1;
        p.add(field, g);
    }

    private void submitProposal() {
        String t = title.getText().trim();
        String d = description.getText().trim();
        if (t.isEmpty() || d.isEmpty()) {
            error.setText("Title and description are required.");
            return;
        }
        error.setText(" ");
        Project p = new Project();
        p.setOwnerId(user.getId());
        p.setGroupName(group.getText().trim());
        p.setTeam(team.getText().trim());
        p.setTitle(t);
        p.setDescription(d);
        p.setTech(tech.getText().trim());
        submit.setEnabled(false);
        Async.run(this, () -> dao.create(p), id -> {
            submit.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Proposal submitted. Waiting for faculty review.",
                    "Submitted", JOptionPane.INFORMATION_MESSAGE);
            reset();
            onCreated.run();
        }, () -> submit.setEnabled(true));
    }

    void reset() {
        title.setText("");
        tech.setText("");
        description.setText("");
        error.setText(" ");
    }
}
