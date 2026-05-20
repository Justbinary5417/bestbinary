package com.justbinary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.justbinary.repository")
public class JustbinaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(JustbinaryApplication.class, args);
    }
}