package com.example.assignmenttwo_starter.controller;

import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.model.Dog;
import com.example.assignmenttwo_starter.service.CustomerService;
import com.example.assignmenttwo_starter.service.DogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dogs")
public class DogController {

    @Autowired
    private DogService dogService;

    @Autowired
    private CustomerService customerService;

    @PostMapping(value = "/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> createDog(@PathVariable Integer customerId, @RequestBody Dog dog) {
        Optional<Customer> optionalCustomer = customerService.findOneCustomer(customerId.longValue());
        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("This customer doesn't exist!");
        }

        Customer customer = optionalCustomer.get();
        dog.setCustomer(customer);
        dogService.saveDog(dog);
        return ResponseEntity.status(HttpStatus.CREATED).body("Your dog " + dog.getName() + " was added");
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getDogById(@PathVariable Long id) {
        Optional<Dog> optionalDog = dogService.findOneDog(id);
        if (!optionalDog.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("This dog doesn't exist!");
        }

        return ResponseEntity.ok(optionalDog.get());
    }

    @Cacheable(value = "dogCache", key = "'getAllDogs'")
    @GetMapping(value = "/all", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Dog>> getAllDogs() {
        List<Dog> dogs = dogService.findAllDogs();
        return ResponseEntity.ok(dogs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDogById(@PathVariable Long id) {
        Optional<Dog> optionalDog = dogService.findOneDog(id);
        if (!optionalDog.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("This dog doesn't exist!");
        }

        dogService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDogById(@PathVariable Long id, @RequestBody Dog updatedDog) {
        Optional<Dog> optionalDog = dogService.findOneDog(id);
        if (!optionalDog.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("This dog doesn't exist!");
        }

        Dog dog = optionalDog.get();
        dog.setName(updatedDog.getName());
        dog.setVaccinated(updatedDog.isVaccinated());
        dogService.saveDog(dog);
        return ResponseEntity.ok(dog);
    }
}