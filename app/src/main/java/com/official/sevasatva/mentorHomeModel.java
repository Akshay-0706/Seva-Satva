package com.official.sevasatva;

public class mentorHomeModel {
    String name;
    String uid;
    String yearNBranch;
    String image;

    public mentorHomeModel() {
    }

    public mentorHomeModel(String name, String uid, String yearNBranch, String image) {
        this.name = name;
        this.uid = uid;
        this.yearNBranch = yearNBranch;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getYearNBranch() {
        return yearNBranch;
    }

    public String getImage() {
        return image;
    }
}
