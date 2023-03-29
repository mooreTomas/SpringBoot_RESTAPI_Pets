package com.example.assignmenttwo_starter.repository;


import com.example.assignmenttwo_starter.model.Dog;
import com.example.assignmenttwo_starter.model.DogShowRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DogShowRegistrationRepository extends JpaRepository <DogShowRegistration, Long> {

    Optional<DogShowRegistration> findByDogAndEventDate(Dog dog, LocalDate eventDate);
}
