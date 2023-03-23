package com.example.assignmenttwo_starter.controller;

import com.example.assignmenttwo_starter.exceptions.OrderNotFoundException;


import com.example.assignmenttwo_starter.model.*;


import com.example.assignmenttwo_starter.service.CustomerService;
import com.example.assignmenttwo_starter.service.CustomerService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.assignmenttwo_starter.service.OrdersService;
import com.example.assignmenttwo_starter.service.ProductService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;



import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import javax.swing.text.html.Option;


@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrdersService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MessageSource messageSource;


    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> list = customerService.findAllCustomers();

        if (list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } else {

            return ResponseEntity.ok(list);
        }
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Customer> getOne(@PathVariable long id) {
        Optional<Customer> aCustomer = customerService.findOneCustomer(id);

        if (!aCustomer.isPresent()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(aCustomer.get());
        }

    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity addCustomer(@RequestBody Customer c) {
        customerService.saveCustomer(c);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity editCustomer(@RequestBody Customer c) {
        customerService.saveCustomer(c);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/{pageNo}/{pageSize}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaTypes.HAL_JSON_VALUE})
    public ResponseEntity getAllPagination(@PathVariable int pageNo, @PathVariable int pageSize) {
        List<Customer> list = customerService.findAllPaginated(pageNo, pageSize);
        if (list.isEmpty()) {
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


                Link customLink = WebMvcLinkBuilder.linkTo(CustomerController.class).slash("order").slash(customer.getCustomerId()).withRel("order");
                customer.add(customLink);
            }

            return new ResponseEntity<>(list, HttpStatus.OK);


        }

    }

    // returns empty if no order exists for that customer
    // contains order information, and order item collection info
    @GetMapping(value = "/order/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Orders>> getCustomerOrderInfo(@PathVariable long id) {
        Optional<Customer> optionalCustomer = customerService.findOneCustomer(id);

        if (optionalCustomer.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            List<Orders> orders = optionalCustomer.get().getOrdersCollection();
            for (Orders order : orders) {
                List<OrderItem> orderItems = order.getOrderItemCollection();
                for (OrderItem orderItem : orderItems) {
                    Product product = orderItem.getProductId();
                    orderItem.setProductId(product);
                }
            }
            return ResponseEntity.ok(orders);
        }
    }






    // get all order to tests prior method
    @GetMapping(value = "/orders", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Orders>> getAllOrders() {
        List<Orders> orders = orderService.findAllOrders();
        for (Orders order : orders) {
            List<OrderItem> orderItems = order.getOrderItemCollection();
            for (OrderItem orderItem : orderItems) {
                Product product = orderItem.getProductId();
                orderItem.setProductId(product);
            }
        }
        return ResponseEntity.ok(orders);
    }




    @DeleteMapping("/{customerId}")
    public ResponseEntity deleteCustomer(@PathVariable long customerId) {
        customerService.deleteById(customerId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/hateoas/{customerId}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity getCustomerHATEOAS(@PathVariable long customerId) {
        Optional<Customer> c = customerService.findOneCustomer(customerId);
        if (!c.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Link link = linkTo(methodOn(CustomerController.class).getAllPagination(1, 10)).withSelfRel();
            c.get().add(link);
            return ResponseEntity.ok(c.get());
        }
    }

    // id 34 has items and order information (for testing)

    @GetMapping(value = "/invoice/{orderId}", produces  = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} )
    public ResponseEntity<String> generateInvoice(@PathVariable long orderId, @RequestHeader(value="Accept-Language", required=false) Locale locale) {

        // find order
        // check status (return bad request if not processing or pending)
        // then get customer and orderItem collection via the customer id in Orders and orderItemCollection in Orders
        // generate basic invoice format with gets from Orders and Customer
        // for loop to get the productId for each orderItem, use this to get Product name(s), quantity, price and total
        // finally generate the footer and return the <String> ResponseEntity

        Optional<Orders> optionalOrder = orderService.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Orders order = optionalOrder.get();
        OrderStatus status = order.getOrderStatusId();

        // Check if the order status is either "pending" or "processing"
        if (!"pending".equals(status.getStatus().toLowerCase()) &&
                !"processing".equals(status.getStatus().toLowerCase())) {
            return ResponseEntity.badRequest().body("Invoice can only be generated for orders with status 'Pending' or 'Processing'");
        } else {

            Customer customer = order.getCustomerId();
            List<OrderItem> items = order.getOrderItemCollection();


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(messageSource.getMessage("date.format", null, locale));
            String formattedDate = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter);


            // Generate the invoice header
            String invoiceTitle = messageSource.getMessage("invoice.title", null, locale);
            String invoice = String.format("%s:\n\n%s: %d\n%s: %s\n%s: %s %s\n\n",
                    invoiceTitle, messageSource.getMessage("invoice.orderId", null, locale), order.getOrderId(),
                    messageSource.getMessage("invoice.date", null, locale), formattedDate,
                    messageSource.getMessage("invoice.customer", null, locale), customer.getFirstName(), customer.getLastName());

            // Generate the invoice body for each order item
            BigDecimal grandTotal = BigDecimal.ZERO;
            for (OrderItem item : items) {
                Product product = item.getProductId();
                BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                grandTotal = grandTotal.add(itemTotal);

                invoice += String.format("%s - %d x $%.2f = $%.2f\n",
                        product.getName(), item.getQuantity(), item.getPrice(), itemTotal);
            }

            // Generate the invoice footer with the grand total
            String localisedTotal = messageSource.getMessage("invoice.total", null, locale);
            invoice += String.format("\n" + localisedTotal +": $%.2f\n", grandTotal);



            String message = messageSource.getMessage("invoice.thanks", null, locale);
            invoice += message;


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<>(invoice, headers, HttpStatus.OK);
        }
    }








    @PutMapping("")
    public ResponseEntity edit(@RequestBody Customer c) {
        customerService.saveCustomer(c);
        return new ResponseEntity<>(HttpStatus.OK);
    }





}










