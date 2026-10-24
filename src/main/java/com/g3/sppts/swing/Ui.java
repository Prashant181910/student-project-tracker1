package com.g3.sppts.swing;

import com.g3.sppts.model.Project;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/** Shared look-and-feel helpers and table cell renderers for the Swing screens. */
final class Ui {
    static final Color BRAND = new Color(0x3b5bdb);

    private Ui() { }

    static Color statusColor(String status) {
        if (status == null) return Color.GRAY;
        switch (status) {
            case Project.APPROVED:  return new Color(0x2f9e44);
            case Project.REJECTED:  return new Color(0xe03131);
            case Project.REVISION:  return new Color(0x7048e8);
            case Project.COMPLETED: return new Color(0x1098ad);
            default:                return new Color(0xf08c00); // Pending
        }
    }

    /** Wraps a component in a panel with a titled border. */
    static JPanel section(String title, JComponent content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(content, BorderLayout.CENTER);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    /** Stops a BoxLayout from stretching a section taller than it needs to be. */
    static void fixHeight(JComponent c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
    }

    static JLabel bold(String text, float size) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, size));
        return l;
    }

    /** Draws the progress column of the project table as a progress bar. */
    static class ProgressRenderer extends JProgressBar implements TableCellRenderer {
        ProgressRenderer() {
            super(0, 100);
            setStringPainted(true);
            setBorderPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
            int value = v instanceof Integer ? (Integer) v : 0;
            setValue(value);
            setString(value + "%");
            return this;
        }
    }

    /** Draws the status column as coloured bold text. */
    static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, focus, r, c);
            String status = String.valueOf(v);
            setText(Project.REVISION.equals(status) ? "Needs revision" : status);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD));
            if (!sel) setForeground(statusColor(status));
            return this;
        }
    }
}
