package com.panda.template.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class LPTagBean {

    List<TagBean> bossTagList;
    List<TagBean> zlTagList;

    public LPTagBean() {
        this.bossTagList = new ArrayList<>();
        this.zlTagList = new ArrayList<>();
    }

    public LPTagBean(List<TagBean> bossTagList, List<TagBean> zlTagList) {
        this.bossTagList = bossTagList;
        this.zlTagList = zlTagList;
    }

}
