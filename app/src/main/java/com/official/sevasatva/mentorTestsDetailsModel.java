package com.official.sevasatva;

import java.util.ArrayList;

public class mentorTestsDetailsModel {
    String studentEmail;
    String studentName;
    String studentBranch;
    String studentClass;
    String studentUID;
    String studentMarks;
    String studentStatus;

    public mentorTestsDetailsModel() {
    }

    public mentorTestsDetailsModel(String studentEmail, String studentName, String studentBranch, String studentClass, String studentUID, String studentMarks, String studentStatus) {
        this.studentEmail = studentEmail;
        this.studentName = studentName;
        this.studentBranch = studentBranch;
        this.studentClass = studentClass;
        this.studentUID = studentUID;
        this.studentMarks = studentMarks;
        this.studentStatus = studentStatus;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentBranch() {
        return studentBranch;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public String getStudentUID() {
        return studentUID;
    }

    public String getStudentMarks() {
        return studentMarks;
    }

    public String getStudentStatus() {
        return studentStatus;
    }
}
