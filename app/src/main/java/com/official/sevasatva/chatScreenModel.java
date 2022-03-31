package com.official.sevasatva;

public class chatScreenModel {
    String date;
    String email;
    Boolean isStudent;
    String msg;
    String name;
    String time;

    public chatScreenModel() {
    }

    public chatScreenModel(String date, String email, Boolean isStudent, String msg, String name, String time) {
        this.date = date;
        this.email = email;
        this.isStudent = isStudent;
        this.msg = msg;
        this.name = name;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIsStudent() {
        return isStudent;
    }

    public String getMsg() {
        return msg;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

}
