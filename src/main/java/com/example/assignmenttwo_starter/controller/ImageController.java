package com.example.assignmenttwo_starter.controller;

import com.example.assignmenttwo_starter.model.Dog;
import com.example.assignmenttwo_starter.model.ImageData;
import com.example.assignmenttwo_starter.service.DogService;
import com.example.assignmenttwo_starter.service.ImageDataService;
import com.example.assignmenttwo_starter.service.StorageService;
import com.example.assignmenttwo_starter.service.CustomerService;
import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.service.AwsStorageService;
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

// Assumption: customer can have multiple dogs, but each name should be unique
@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private StorageService service;


    @Autowired
    private CustomerService customerService;

    @Autowired
    private ImageDataService imageDataService;

    @Autowired
    private DogService dogService;

    @Autowired
    private AwsStorageService awsStorageService;

    // 1 image per dog (customer can have more than 1 dog)
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

        // Check if image already exists for dog
        if (!dog.getImages().isEmpty()) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Already have an image! Delete it before uploading a new image for");
            }


        ImageData imageData = new ImageData();
        // Associate the image with the dog
        imageData.setDog(dog);
        imageData.setImageName(file.getOriginalFilename());
        imageData.setImageType(file.getContentType());

        try {
            imageData.setImageData(file.getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while processing the image");
        }

        imageDataService.save(imageData);

        // uploads to AWS bucket as well
        // rename before uploading to AWS, so they can be identified
        String newFileName = customerOptional.get().getCustomerId() + "_" + dog.getName();
        awsStorageService.uploadFile(file, newFileName);

        String ownerName = customerOptional.get().getFirstName();
        return ResponseEntity.status(HttpStatus.CREATED).body("Image " + imageData.getName() + " of " + ownerName + "'s dog, " + dog.getName() + " was successfully uploaded");
    }

    @GetMapping("/{customerId}/{dogName}")
    public ResponseEntity<?> downloadImage(@PathVariable Integer customerId, @PathVariable String dogName) {
        Optional<Customer> customerOptional = customerService.findOneCustomer(Long.valueOf(customerId));
        if (!customerOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Optional<Dog> dogOptional = dogService.findDogByNameAndCustomerId(dogName, customerId);
        if (!dogOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dog not found, try again!");
        }

        Dog dog = dogOptional.get();

        // Find the image associated with the dog
        ImageData imageData = null;
        for (ImageData img : dog.getImages()) {
            imageData = img;
            break;
        }

        if (imageData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found for the specified dog");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(imageData.getType()))
                .body(imageData.getImageData());
    }

    @DeleteMapping("/{customerId}/{dogName}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer customerId, @PathVariable String dogName) {
        Optional<Customer> customerOptional = customerService.findOneCustomer(Long.valueOf(customerId));
        if (!customerOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Optional<Dog> dogOptional = dogService.findDogByNameAndCustomerId(dogName, customerId);
        if (!dogOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dog not found, try again!");
        }

        Dog dog = dogOptional.get();

        // Find the image associated with the dog
        ImageData imageData = null;
        for (ImageData img : dog.getImages()) {
            imageData = img;
            break;
        }

        if (imageData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No image found for " + dog.getName());
        }

        // Remove the image from the dog's images list and save the dog
        // used this roundabout method because a simple "delete by id" was not working
        dog.removeImage(imageData);
        dogService.save(dog);

        imageDataService.deleteImage(imageData.getId());
        return ResponseEntity.status(HttpStatus.OK).body(customerOptional.get().getFirstName() + "'s" + " dog " + dogOptional.get().getName() + "'s associated image deleted, make sure to upload another image if you want one on the system!");
    }










}
