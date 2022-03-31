package com.official.sevasatva;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class studentHomeAnsModel {

    String title;
    String desc;
    Boolean hasAttach;
    Boolean isExpanded;
    ArrayList<String> attach;
    String id;



    public studentHomeAnsModel() {
    }

    public studentHomeAnsModel(String title, String desc, Boolean hasAttach, Boolean isExpanded,ArrayList<String> attach, String id) {
        this.title = title;
        this.desc = desc;
        this.hasAttach = hasAttach;
        this.isExpanded = isExpanded;
        this.attach = attach;
        this.id = id;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public Boolean getHasAttach() {
        return hasAttach;
    }

    public Boolean getExpanded() {
        return isExpanded;
    }

    public ArrayList<String> getAttach() {
        return attach;
    }

    public String getId() {
        return id;
    }
}
