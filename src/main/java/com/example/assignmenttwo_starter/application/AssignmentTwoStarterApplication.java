package com.example.assignmenttwo_starter.application;

import com.example.assignmenttwo_starter.config.PdfReportGenerator;
import com.example.assignmenttwo_starter.repository.*;
import com.example.assignmenttwo_starter.service.AwsStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;


@SpringBootApplication
@EnableSwagger2
@EnableCaching
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

    @Autowired
    private DogShowRegistrationRepository dogShowRegistrationRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    // for internationalisation of invoice generator
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }


    @Bean
    public PdfReportGenerator pdfReportGenerator() {
        return new PdfReportGenerator();
    }

    // manages caching
    // decided to use multiple caches, 1 for each controller
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("customerCache"),
                new ConcurrentMapCache("dogCache"),
                new ConcurrentMapCache("dogShowCache"),
                new ConcurrentMapCache("imageCache"),
                new ConcurrentMapCache("storageCache")
        ));
        return cacheManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(AssignmentTwoStarterApplication.class, args);
    }

}
