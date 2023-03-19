package com.example.assignmenttwo_starter.controller;


import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.service.CustomerService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;


@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> list = customerService.findAllCustomers();

        if(list.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } else {

            return ResponseEntity.ok(list);
        }
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Customer> getOne(@PathVariable long id){
        Optional<Customer> aCustomer = customerService.findOneCustomer(id);

        if (!aCustomer.isPresent()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(aCustomer.get());
        }

    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity addCustomer (@RequestBody Customer c){
        customerService.saveCustomer(c);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity editCustomer  (@RequestBody Customer c){
        customerService.saveCustomer(c);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/{pageNo}/{pageSize}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaTypes.HAL_JSON_VALUE} )
    public ResponseEntity getAllPagination(@PathVariable int pageNo, @PathVariable int pageSize){
        List<Customer> list = customerService.findAllPaginated(pageNo, pageSize);
        if(list.isEmpty()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        } else {

            Link selfLink = linkTo(methodOn(CustomerController.class).getAllPagination(pageNo, pageSize)).withSelfRel();
            List<EntityModel<Customer>> customerModels = list.stream()
                    .map(customer -> EntityModel.of(customer, linkTo(methodOn(CustomerController.class).getOne(customer.getCustomerId())).withSelfRel()))
                    .collect(Collectors.toList());

            // create a CollectionModel to represent the collection of customers
            CollectionModel<EntityModel<Customer>> collectionModel = CollectionModel.of(customerModels, selfLink);

            for (Customer customer : list) {
                // Generate self link
                Link selfLink2 = linkTo(methodOn(CustomerController.class).getOne(customer.getCustomerId())).withSelfRel();
                customer.add(selfLink);

                // Generate custom link
                Link customLink = WebMvcLinkBuilder.linkTo(CustomerController.class).slash("order").slash(customer.getCustomerId()).withRel("order");
                customer.add(customLink);
            }

            return new ResponseEntity<>(list, HttpStatus.OK);


        }

    }

    @GetMapping(value = "/order/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Customer>> getCustomerOrderInfo(@PathVariable String id){
        List<Customer> list = customerService.getOrdersByCustomerId(id);

        if(list.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } else {

            return ResponseEntity.ok(list);
        }




    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity deleteCustomer(@PathVariable long customerId){
        customerService.deleteById(customerId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/hateoas/{customerId}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity getCustomerHATEOAS(@PathVariable long customerId){
        Optional<Customer> c = customerService.findOneCustomer(customerId);
        if(!c.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{
            Link link = linkTo(methodOn(CustomerController.class).getAllPagination(1,10)).withSelfRel();
            c.get().add(link);
            return ResponseEntity.ok(c.get());
        }
    }

    @PutMapping("")
    public ResponseEntity edit(@RequestBody Customer c){
        customerService.saveCustomer(c);
        return new ResponseEntity<>(HttpStatus.OK);
    }









    }








