package com.official.sevasatva;

public class mentorAllocatorModel {
    String name;
    String email;
    String pass;
    String studentCount;
    boolean isExpanded;

    public mentorAllocatorModel() {
    }

    public mentorAllocatorModel(String name, String email, String pass, String studentCount, boolean isExpanded) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.studentCount = studentCount;
        this.isExpanded = isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getStudentCount() {
        return studentCount;
    }

    public boolean getExpanded() {
        return isExpanded;
    }
}
