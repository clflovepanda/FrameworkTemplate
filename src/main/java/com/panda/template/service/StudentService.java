package com.panda.template.service;

import com.panda.template.dao.StudentDao;
import com.panda.template.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentDao studentDao;

    public Student getStudent(int id) {
        return studentDao.getById(id);
    }
}
