package com.example.assignmenttwo_starter.application;

import com.example.assignmenttwo_starter.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"com.example.assignmenttwo_starter.service", "com.example.assignmenttwo_starter.controller", "com.example.assignmenttwo_starter.repository"})
@EntityScan("com.example.assignmenttwo_starter.model")
@EnableJpaRepositories("com.example.assignmenttwo_starter.repository")

public class AssignmentTwoStarterApplication {

    @Autowired
    private CustomerRepository customerRepo;

    public static void main(String[] args) {
        SpringApplication.run(AssignmentTwoStarterApplication.class, args);
    }

}
