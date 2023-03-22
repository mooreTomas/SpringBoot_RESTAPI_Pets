package com.example.assignmenttwo_starter.repository;

import com.example.assignmenttwo_starter.model.OrderItem;
import org.python.antlr.op.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderItemRepository extends JpaRepository <OrderItem, Long> {

    //List<OrderItem> findOrderItemByOrOrderByOrderId(String id);


}
