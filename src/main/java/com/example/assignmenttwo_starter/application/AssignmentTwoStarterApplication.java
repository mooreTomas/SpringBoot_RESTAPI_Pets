package com.example.assignmenttwo_starter.application;

import com.example.assignmenttwo_starter.repository.*;
import com.example.assignmenttwo_starter.service.AwsStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;




@SpringBootApplication
@ComponentScan({"com.example.assignmenttwo_starter.service", "com.example.assignmenttwo_starter.controller", "com.example.assignmenttwo_starter.repository", "com.example.assignmenttwo_starter.config"})
@EntityScan("com.example.assignmenttwo_starter.model")
@EnableJpaRepositories("com.example.assignmenttwo_starter.repository")


public class AssignmentTwoStarterApplication {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DogRepository dogRepository;


    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(AssignmentTwoStarterApplication.class, args);
    }

}
