package com.example.assignmenttwo_starter.service;

import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public void saveCustomer(Customer c){
        customerRepo.save(c);
    }

    public List<Customer> findAllPaginated(int pageNo, int pageSize){
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<Customer> pagedResult = customerRepo.findAll(paging);
        return pagedResult.toList();
    }

    public void deleteById(long customerId){
        customerRepo.deleteById(customerId);
    }


}
