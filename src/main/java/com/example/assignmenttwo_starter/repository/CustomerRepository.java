package com.example.assignmenttwo_starter.repository;

import com.example.assignmenttwo_starter.model.Customer;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {




    @Query("SELECT c FROM Customer c JOIN FETCH c.ordersCollection o WHERE c.customerId = :customerId")
    List<Customer> getOrdersByCustomerId(@Param("customerId") String customerId);






}
