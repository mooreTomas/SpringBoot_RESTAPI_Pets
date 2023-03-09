package com.example.assignmenttwo_starter.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.example.assignmenttwo_starter.model")
public class AssignmentTwoStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssignmentTwoStarterApplication.class, args);
    }

}
