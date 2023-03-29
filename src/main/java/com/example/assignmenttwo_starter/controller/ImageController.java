package com.example.assignmenttwo_starter.controller;

import com.example.assignmenttwo_starter.model.Dog;
import com.example.assignmenttwo_starter.model.ImageData;
import com.example.assignmenttwo_starter.service.DogService;
import com.example.assignmenttwo_starter.service.ImageDataService;
import com.example.assignmenttwo_starter.service.StorageService;
import com.example.assignmenttwo_starter.service.CustomerService;
import com.example.assignmenttwo_starter.model.Customer;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private StorageService service;

    // Autowire CustomerService
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ImageDataService imageDataService;

    @Autowired
    private DogService dogService;

    @PostMapping("/{customerId}/{dogName}")
    public ResponseEntity<?> uploadImage(@PathVariable Integer customerId, @PathVariable String dogName, @RequestParam("file") MultipartFile file) {
        Optional<Customer> customerOptional = customerService.findOneCustomer(Long.valueOf(customerId));
        if (!customerOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Optional<Dog> dogOptional = dogService.findDogByNameAndCustomerId(dogName, customerId);
        if (!dogOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dog not found, try again!");
        }

        Dog dog = dogOptional.get();

        // Process the image and save it
        ImageData imageData = new ImageData();
        imageData.setDog(dog); // Associate the image with the dog
        imageData.setImageName(file.getOriginalFilename());
        imageData.setImageType(file.getContentType());

        try {
            imageData.setImageData(file.getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while processing the image");
        }

        imageDataService.save(imageData);

        String ownerName = customerOptional.get().getFirstName();
        return ResponseEntity.status(HttpStatus.CREATED).body("An image of " + ownerName + "'s dog, " + dog.getName() + " was successfully uploaded");
    }


    // returns specific image
    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName){
        byte[] imageData=service.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);

    }




}
