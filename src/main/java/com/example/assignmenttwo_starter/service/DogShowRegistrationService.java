package com.example.assignmenttwo_starter.service;


import com.example.assignmenttwo_starter.model.Dog;
import com.example.assignmenttwo_starter.model.DogShowRegistration;
import com.example.assignmenttwo_starter.repository.DogShowRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DogShowRegistrationService {

    @Autowired
    private DogShowRegistrationRepository dogShowRegistrationRepository;


    public void saveDogShowRegistration(DogShowRegistration r){
        dogShowRegistrationRepository.save(r);
    }

    public Optional<DogShowRegistration> findRegistrationById(Long id){
        return dogShowRegistrationRepository.findById(id);
    }

    public void deleteRegistration(long dogId){
        dogShowRegistrationRepository.deleteById(dogId);
    }

    public Optional<DogShowRegistration> findRegistrationByDogAndEventDate(Dog dog, LocalDate eventDate) {
        return dogShowRegistrationRepository.findByDogAndEventDate(dog, eventDate);
    }
}
