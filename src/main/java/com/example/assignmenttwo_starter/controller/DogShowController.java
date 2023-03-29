package com.example.assignmenttwo_starter.controller;

import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.model.Dog;
import com.example.assignmenttwo_starter.model.DogShowRegistration;
import com.example.assignmenttwo_starter.service.CustomerService;
import com.example.assignmenttwo_starter.service.DogService;
import com.example.assignmenttwo_starter.service.DogShowRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/dog-show")
public class DogShowController {

    @Autowired
    private DogShowRegistrationService dogShowRegistrationService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DogService dogService;

    @PostMapping("/register/{customerId}/{dogName}")
    public ResponseEntity<?> registerDogForShow(@PathVariable Integer customerId, @PathVariable String dogName, @RequestParam("eventDate") String eventDateString) {
        Optional<Customer> customerOptional = customerService.findOneCustomer(Long.valueOf(customerId));
        if (!customerOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Optional<Dog> dogOptional = dogService.findDogByNameAndCustomerId(dogName, customerId);
        if (!dogOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dog not found, try again!");
        }

        Dog dog = dogOptional.get();
        if (!dog.isVaccinated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your dog " + dog.getName() + " is not vaccinated, can't enter dog show");
        }

        LocalDate eventDate = LocalDate.parse(eventDateString);

        // Check if the dog is already registered for the event date
        Optional<DogShowRegistration> existingRegistration = dogShowRegistrationService.findRegistrationByDogAndEventDate(dog, eventDate);
        if (existingRegistration.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Dog is already registered for this event date");
        }

        DogShowRegistration registration = new DogShowRegistration(null, eventDate, customerOptional.get(), dog);
        dogShowRegistrationService.saveDogShowRegistration(registration);

        return ResponseEntity.status(HttpStatus.CREATED).body("Dog successfully registered for the show");
    }


    @GetMapping("/{registrationId}")
    public ResponseEntity<?> getRegistration(@PathVariable Long registrationId) {
        Optional<DogShowRegistration> registrationOptional = dogShowRegistrationService.findRegistrationById(registrationId);
        if (!registrationOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found");
        }

        return ResponseEntity.ok(registrationOptional.get());
    }

    @DeleteMapping("/{registrationId}")
    public ResponseEntity<?> deleteRegistration(@PathVariable Long registrationId) {
        Optional<DogShowRegistration> registrationOptional = dogShowRegistrationService.findRegistrationById(registrationId);
        if (!registrationOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found");
        }

        dogShowRegistrationService.deleteRegistration(registrationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
