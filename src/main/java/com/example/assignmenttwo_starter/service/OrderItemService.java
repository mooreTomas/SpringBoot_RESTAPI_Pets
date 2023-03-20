package com.example.assignmenttwo_starter.service;


import com.example.assignmenttwo_starter.model.OrderItem;
import com.example.assignmenttwo_starter.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderItem> findOrderItemByOrOrderByOrderId(String id){
        return orderItemRepository.findOrderItemByOrOrderByOrderId(id);
    }
}
