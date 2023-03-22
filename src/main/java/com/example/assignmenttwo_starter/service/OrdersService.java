package com.example.assignmenttwo_starter.service;

import com.example.assignmenttwo_starter.model.Orders;
import com.example.assignmenttwo_starter.repository.CustomerRepository;
import com.example.assignmenttwo_starter.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    public Optional<Orders> findById (long id) {
        return ordersRepository.findById(id);
    }


    public List<Orders> findAllOrders() {
        return ordersRepository.findAll();
    }
}
