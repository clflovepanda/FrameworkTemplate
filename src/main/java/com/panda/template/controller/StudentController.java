package com.panda.template.controller;

import com.panda.template.biz.OriginDataBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class StudentController {

    @Autowired
    private OriginDataBiz originDataBiz;

    @RequestMapping(value = "getStudent", method = RequestMethod.GET)
    public void studentController(@RequestParam("id") int id,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        response.getWriter().write("");
    }

}
