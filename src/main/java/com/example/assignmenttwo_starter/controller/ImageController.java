package com.example.assignmenttwo_starter.controller;

import com.example.assignmenttwo_starter.model.ImageData;
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

    @PostMapping("/{customerId}")
    public ResponseEntity<?> uploadImage(@PathVariable Integer customerId, @RequestParam("image") MultipartFile file) throws IOException {
        Optional<Customer> optionalCustomer = customerService.findOneCustomer(customerId.longValue());
        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("This customer doesn't exist!");
        }

        // Get the original file name (e.g., 1111.png)
        String originalFileName = file.getOriginalFilename();

        Customer customer = optionalCustomer.get();

        // Create ImageData instance
        ImageData imageData = ImageData.builder()
                .name(originalFileName)
                .type(file.getContentType())
                .imageData(file.getBytes())
                .customer(customer)
                .build();

        // Save ImageData instance
        imageDataService.save(imageData);

        // Associate customer with the uploaded image
        customer.setImageData(imageData);
        customerService.saveCustomer(customer);

        return ResponseEntity.status(HttpStatus.OK)
                .body("Image uploaded successfully: " + originalFileName);
    }


    // returns specific image
    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName){
        byte[] imageData=service.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);

    }

    // returns images associated with a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getImageByCustomerId(@PathVariable Integer customerId) {
        Optional<Customer> optionalCustomer = customerService.findOneCustomer(customerId.longValue());
        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("This customer doesn't exist!");
        }

        Customer customer = optionalCustomer.get();
        ImageData imageData = customer.getImageData();

        if (imageData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("No image(s) associated with this customer!");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(imageData.getType()))
                .body(imageData.getImageData());
    }


}
