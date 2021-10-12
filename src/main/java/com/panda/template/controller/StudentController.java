package com.panda.template.controller;

import com.alibaba.fastjson.JSON;
import com.panda.template.bean.LPTagBean;
import com.panda.template.bean.NodeBean;
import com.panda.template.biz.OriginDataBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private OriginDataBiz originDataBiz;

    @RequestMapping(value = "/getNode", method = RequestMethod.GET)
    public void getNode(@RequestParam("name") String name,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        List<NodeBean> result = originDataBiz.getNode(name);
        response.getWriter().write(URLEncoder.encode(JSON.toJSONString(result)));
    }

    @RequestMapping(value = "/getTags", method = RequestMethod.GET)
    public void getTags(@RequestParam("name") String name,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        System.out.println(name);
        LPTagBean result = originDataBiz.getTags(name);
        System.out.println(JSON.toJSONString(result));
        response.getWriter().write(URLEncoder.encode(JSON.toJSONString(result)));
    }

}
