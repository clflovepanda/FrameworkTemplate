package com.panda.template.biz;

import com.panda.template.bean.NodeTree;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OriginDataBiz {

    private NodeTree bossTree;

    private NodeTree zlTree;

    private NodeTree lpTree;

    @PostConstruct
    private void init() {

    }



}
