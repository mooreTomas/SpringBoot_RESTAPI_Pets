package com.example.assignmenttwo_starter.service;

import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    public Optional<Customer> findOneCustomer(Long id){
        return customerRepo.findById(id);
    }

    public List<Customer> findAllCustomers(){
        return (List<Customer>) customerRepo.findAll();
    }

    public long count(){
        return customerRepo.count();
    }


}
