package com.official.sevasatva;

import java.util.Map;

public class studentTestsModel {
    String title;
    String marks;
    String submitted;
    String deadline;
    boolean onlineStatus;
    Map<String, Object> students;
    String id;

    public studentTestsModel() {
    }

    public studentTestsModel(String title, String marks, String submitted, String deadline, boolean onlineStatus, Map<String, Object> students, String id) {
        this.title = title;
        this.marks = marks;
        this.submitted = submitted;
        this.deadline = deadline;
        this.onlineStatus = onlineStatus;
        this.students = students;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getMarks() {
        return marks;
    }

    public String getSubmitted() {
        return submitted;
    }

    public String getDeadline() {
        return deadline;
    }

    public boolean getOnlineStatus() {
        return onlineStatus;
    }

    public Map<String, Object>  getStudents() {
        return students;
    }

    public String getId() {
        return id;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}
