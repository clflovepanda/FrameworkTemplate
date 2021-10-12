package com.panda.template.bean;

import java.util.ArrayList;
import java.util.List;

public class NodeTree {

    private List<NodeBean> roots;

    public NodeTree() {
        this.roots = new ArrayList<>();
    }

    public List<NodeBean> getRoots() {
        return roots;
    }

    public void setRoots(List<NodeBean> roots) {
        this.roots = roots;
    }
}

