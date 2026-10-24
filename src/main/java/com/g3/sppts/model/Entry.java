package com.g3.sppts.model;

import java.io.Serializable;

/** A dated text entry: used for both student progress updates and faculty feedback. */
public class Entry implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String date;
    private String author; // only set for feedback
    private String body;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
