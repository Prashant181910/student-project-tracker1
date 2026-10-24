package com.g3.sppts.model;

import java.io.Serializable;

/** A tracked step of a project. */
public class Milestone implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int projectId;
    private String title;
    private String dueDate; // yyyy-MM-dd or null
    private boolean done;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
}
