package com.panda.template;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:conf/database.xml")
@MapperScan("com.panda.template.dao")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
