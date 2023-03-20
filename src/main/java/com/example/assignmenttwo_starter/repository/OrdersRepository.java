package com.example.assignmenttwo_starter.repository;

import java.util.List;

import com.example.assignmenttwo_starter.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {














}
