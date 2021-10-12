package com.panda.template.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagBean {

    private String name;
    private String group;
    private int type;

    public TagBean(String name, String group) {
        this.name = name;
        this.group = group;
    }
    public TagBean(String name, String group, int type) {
        this.name = name;
        this.group = group;
        this.type = type;
    }
}
