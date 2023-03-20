package com.example.assignmenttwo_starter.service;

import com.example.assignmenttwo_starter.model.Orders;
import com.example.assignmenttwo_starter.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrdersService {

    @Autowired
    private OrdersRepository orderRepository;




}
