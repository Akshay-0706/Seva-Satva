package com.official.sevasatva;

public class mentorAllocationModel {
    String name, code, desc;

    public mentorAllocationModel() {
    }

    public mentorAllocationModel(String name, String code, String desc) {
        this.name = name;
        this.code = code;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
