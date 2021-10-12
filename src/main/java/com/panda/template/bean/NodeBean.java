package com.panda.template.bean;

import java.util.ArrayList;
import java.util.List;

public class NodeBean {

    private String name;
    private List<NodeBean> children;

    public NodeBean() {
        this.children = new ArrayList<>();
    }

    public NodeBean(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NodeBean> getChildren() {
        return children;
    }

    public void setChildren(List<NodeBean> children) {
        this.children = children;
    }
}
