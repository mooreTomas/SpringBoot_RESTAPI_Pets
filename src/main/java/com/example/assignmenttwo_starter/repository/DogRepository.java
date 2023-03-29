package com.example.assignmenttwo_starter.repository;


import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.model.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {

    // Dog has Customer **object**
    Optional<Dog> findDogByNameAndCustomerCustomerId(String dogName, Integer customerId);


}
