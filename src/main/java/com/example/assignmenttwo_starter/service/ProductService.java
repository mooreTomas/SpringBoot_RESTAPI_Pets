package com.example.assignmenttwo_starter.service;

import com.example.assignmenttwo_starter.model.Product;
import com.example.assignmenttwo_starter.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;


    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
}
