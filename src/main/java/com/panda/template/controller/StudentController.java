package com.panda.template.controller;

import com.alibaba.fastjson.JSON;
import com.panda.template.entity.Student;
import com.panda.template.service.StudentService;
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
    private StudentService studentService;

    @RequestMapping(value = "getStudent", method = RequestMethod.GET)
    public void studentController(@RequestParam("id") int id,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {

        Student student = studentService.getStudent(id);
        response.getWriter().write(JSON.toJSONString(student));

    }

}
