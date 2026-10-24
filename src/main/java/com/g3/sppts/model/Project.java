package com.g3.sppts.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** A student project proposal together with its milestones, updates and feedback. */
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PENDING = "Pending";
    public static final String APPROVED = "Approved";
    public static final String REJECTED = "Rejected";
    public static final String REVISION = "Revision";
    public static final String COMPLETED = "Completed";

    private int id;
    private int ownerId;
    private String ownerName;
    private String groupName;
    private String title;
    private String description;
    private String tech;
    private String team;
    private String status;
    private String submitted;
    private List<Milestone> milestones = new ArrayList<>();
    private List<Entry> updates = new ArrayList<>();
    private List<Entry> feedback = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTech() { return tech; }
    public void setTech(String tech) { this.tech = tech; }
    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSubmitted() { return submitted; }
    public void setSubmitted(String submitted) { this.submitted = submitted; }
    public List<Milestone> getMilestones() { return milestones; }
    public void setMilestones(List<Milestone> milestones) { this.milestones = milestones; }
    public List<Entry> getUpdates() { return updates; }
    public void setUpdates(List<Entry> updates) { this.updates = updates; }
    public List<Entry> getFeedback() { return feedback; }
    public void setFeedback(List<Entry> feedback) { this.feedback = feedback; }

    /** Percentage of milestones that are done (0 when there are none). */
    public int getProgress() {
        if (milestones.isEmpty()) return 0;
        long done = milestones.stream().filter(Milestone::isDone).count();
        return (int) Math.round(done * 100.0 / milestones.size());
    }

    public String getStatusLabel() {
        return REVISION.equals(status) ? "Needs revision" : status;
    }
}
