package com.example.assignmenttwo_starter.controller;

import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.model.Dog;
import com.example.assignmenttwo_starter.service.CustomerService;
import com.example.assignmenttwo_starter.service.DogService;

//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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

//    @ApiOperation(value = "Create a new dog", notes = "Creates a new dog for the specified customer ID")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "Dog created successfully"),
//            @ApiResponse(responseCode = "404", description = "Customer not found")
//    })
    @CacheEvict(value = "dogCache", key = "'getAllDogs'")
    @PostMapping(value = "/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(description = "Creates a dog, and associates it with a customer based on specified id.")
    public ResponseEntity<?> createDog(
            @PathVariable Integer customerId, @RequestBody Dog dog) {

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
    @Operation(description = "Get dog based on specified id")
    public ResponseEntity<?> getDogById(@PathVariable Long id) {
        Optional<Dog> optionalDog = dogService.findOneDog(id);
        if (!optionalDog.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("This dog doesn't exist!");
        }

        return ResponseEntity.ok(optionalDog.get());
    }


    @Cacheable(value = "dogCache", key = "'getAllDogs'")
    @Operation(description = "Return all dogs")
    @GetMapping(value = "/all", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    @ApiOperation(value = "Get all dogs", notes = "Retrieves a list of all dogs")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Dogs retrieved successfully")
//    })
    public ResponseEntity<List<Dog>> getAllDogs() {
        List<Dog> dogs = dogService.findAllDogs();
        return ResponseEntity.ok(dogs);
    }


//    @ApiOperation(value = "Delete dog by ID", notes = "Deletes the dog with the specified ID.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "Dog deleted successfully"),
//            @ApiResponse(responseCode = "404", description = "Dog not found")
//    })
    @CacheEvict(value = "dogCache", key = "'getAllDogs'")
    @DeleteMapping("/{id}")
    @Operation(description = "Delete dog based on id")
    public ResponseEntity<?> deleteDogById(@PathVariable Long id) {
        Optional<Dog> optionalDog = dogService.findOneDog(id);
        if (!optionalDog.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("This dog doesn't exist!");
        }

        dogService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @CacheEvict(value = "dogCache", key = "'getAllDogs'")
    @PutMapping("/{id}")
    @Operation(description = "update a dog absed on id")
    public ResponseEntity<?> updateDogById(
            @PathVariable Long id,
            @RequestBody Dog updatedDog) {

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