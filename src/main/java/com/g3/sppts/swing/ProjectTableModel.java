package com.g3.sppts.swing;

import com.g3.sppts.model.Project;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/** Table model for the project list: one row per project. */
public class ProjectTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = { "Title", "Group", "Student", "Status", "Progress" };
    private List<Project> rows = new ArrayList<>();

    public void setProjects(List<Project> projects) {
        this.rows = new ArrayList<>(projects);
        fireTableDataChanged();
    }

    public Project getProject(int row) {
        return rows.get(row);
    }

    public int indexOfId(int id) {
        for (int i = 0; i < rows.size(); i++) if (rows.get(i).getId() == id) return i;
        return -1;
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int c) { return COLUMNS[c]; }

    @Override
    public Class<?> getColumnClass(int c) {
        return c == 4 ? Integer.class : String.class;
    }

    @Override
    public Object getValueAt(int r, int c) {
        Project p = rows.get(r);
        switch (c) {
            case 0: return p.getTitle();
            case 1: return p.getGroupName();
            case 2: return p.getOwnerName();
            case 3: return p.getStatus();
            default: return p.getProgress();
        }
    }
}
