package com.example.assignmenttwo_starter.service;

import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.model.Dog;
import com.example.assignmenttwo_starter.repository.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DogService {

    @Autowired
    private DogRepository dogRepository;


    public void saveDog(Dog d){
        dogRepository.save(d);
    }

    public Optional<Dog> findOneDog(Long id){
        return dogRepository.findById(id);
    }

    public List<Dog> findAllDogs(){
        return dogRepository.findAll();
    }

    public void deleteById(long dogId){
        dogRepository.deleteById(dogId);
    }
}
