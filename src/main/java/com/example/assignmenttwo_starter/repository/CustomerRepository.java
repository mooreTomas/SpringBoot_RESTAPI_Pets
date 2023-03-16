package com.example.assignmenttwo_starter.repository;

import com.example.assignmenttwo_starter.model.Customer;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository <Customer, Long>{




}
