package com.panda.template.service;

import com.panda.template.dao.StudentDao;
import com.panda.template.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class StudentService {

    @PostConstruct
    public void init() {

    }

    @Autowired
    private StudentDao studentDao;

}
