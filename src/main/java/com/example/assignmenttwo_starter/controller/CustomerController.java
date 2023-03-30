package com.example.assignmenttwo_starter.controller;

import com.example.assignmenttwo_starter.exceptions.InvoiceErrorException;


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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
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


import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;


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

    @Cacheable(value = "customerCache", key = "'getAllCustomers'")
    @ApiOperation(value = "Get all customers", notes = "Returns a list of all customers in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of customers"),
            @ApiResponse(code = 404, message = "No customers found")
    })
    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> list = customerService.findAllCustomers();

        if (list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } else {

            return ResponseEntity.ok(list);
        }
    }


    @GetMapping(value = "/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ApiOperation(value = "Get a single customer", notes = "Returns a single customer by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved customer"),
            @ApiResponse(code = 404, message = "Customer not found")
    })
    public ResponseEntity getOneCustomer(@PathVariable long customerId) {
        Optional<Customer> c = customerService.findOneCustomer(customerId);
        if (!c.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            // gets all with pagination appropriate to the customerId (instead of listing everything in one result)
            long paginationValue = customerId/10 + 1;
            Link link = linkTo(methodOn(CustomerController.class).getAllPagination((int) paginationValue, 10)).withSelfRel();
            c.get().add(link);
            return ResponseEntity.ok(c.get());
        }
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ApiOperation(value = "Add a new customer", notes = "Creates a new customer in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Customer created successfully"),
            @ApiResponse(code = 400, message = "Invalid input data")
    })
    public ResponseEntity addCustomer(@RequestBody Customer c) {
        customerService.saveCustomer(c);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ApiOperation(value = "Edit an existing customer", notes = "Updates an existing customer in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer updated successfully"),
            @ApiResponse(code = 400, message = "Invalid input data")
    })
    public ResponseEntity editCustomer(@RequestBody Customer c) {
        customerService.saveCustomer(c);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/{pageNo}/{pageSize}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaTypes.HAL_JSON_VALUE})
    @ApiOperation(value = "Get a paginated list of customers", notes = "Returns a list of customers with pagination support")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved paginated customer list"),
            @ApiResponse(code = 404, message = "No customers found for the given page number and page size")
    })
    public ResponseEntity getAllPagination(@PathVariable int pageNo, @PathVariable int pageSize) {
        List<Customer> list = customerService.findAllPaginated(pageNo, pageSize);
        if (list.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        } else {

            Link selfLink = linkTo(methodOn(CustomerController.class).getAllPagination(pageNo, pageSize)).withSelfRel();
            List<EntityModel<Customer>> customerModels = list.stream()
                    .map(customer -> EntityModel.of(customer, linkTo(methodOn(CustomerController.class).getOneCustomer(customer.getCustomerId())).withSelfRel()))
                    .collect(Collectors.toList());

            // create a CollectionModel to represent the collection of customers
            CollectionModel<EntityModel<Customer>> collectionModel = CollectionModel.of(customerModels, selfLink);

            for (Customer customer : list) {
                // Generate self link
                Link selfLink2 = linkTo(methodOn(CustomerController.class).getOneCustomer(customer.getCustomerId())).withSelfRel();
                customer.add(selfLink);


                Link customLink = WebMvcLinkBuilder.linkTo(CustomerController.class).slash("order").slash(customer.getCustomerId()).withRel("order");
                customer.add(customLink);
            }

            return new ResponseEntity<>(list, HttpStatus.OK);


        }

    }

    // returns error String if customer doesn't exist
    // returns error String if order exists but collection is empty
    // otherwise returns order with associated products

    @GetMapping(value = "/order/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ApiOperation(value = "Get customer order information", notes = "Returns a list of orders for a specific customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved customer order information"),
            @ApiResponse(code = 404, message = "No orders found for the given customer ID")
    })
    public ResponseEntity<?> getCustomerOrderInfo(@PathVariable long id) {
        Optional<Customer> optionalCustomer = customerService.findOneCustomer(id);
        if (!optionalCustomer.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("This customer doesn't exist!");
        } else {
            List<Orders> orders = optionalCustomer.get().getOrdersCollection();
            if (orders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("No orders found for Customer " + id + "!");
            }
            for (Orders order : orders) {
                List<OrderItem> orderItems = order.getOrderItemCollection();
                if (orderItems.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.TEXT_PLAIN)
                            .body("Order(s) exist(s), but order item collection is empty!");
                }
                for (OrderItem orderItem : orderItems) {
                    Product product = orderItem.getProductId();
                    orderItem.setProductId(product);
                }
            }
            return ResponseEntity.ok(orders);
        }
    }




    // get all order to tests prior method
    @Cacheable (value = "customerCache", key = "'getAllOrders'")
    @GetMapping(value = "/orders", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ApiOperation(value = "Get a list of all orders", notes = "Returns a list of all orders")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of all orders")
    })
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
    @ApiOperation(value = "Delete a customer by ID", notes = "Deletes a customer with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer successfully deleted"),
            @ApiResponse(code = 404, message = "Customer not found for the given ID")
    })
    public ResponseEntity deleteCustomer(@PathVariable long customerId) {
        customerService.deleteById(customerId);
        return new ResponseEntity(HttpStatus.OK);
    }



    // id 34 has items and order information (for testing)



    @GetMapping(value = "/invoice/{orderId}", produces = {MediaType.APPLICATION_PDF_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ApiOperation(value = "Generate invoice", notes = "Generates a PDF invoice for the specified order. Returns the PDF file as a byte array.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Invoice generated successfully"),
            @ApiResponse(code = 400, message = "Order status is not 'processing' or 'pending', or order item collection is empty"),
            @ApiResponse(code = 404, message = "Order not found")
    })

    public ResponseEntity<?> generateInvoice(@PathVariable long orderId, @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

        // find order, check if exists first
        // check status (return bad request if not processing or pending)
        // then get customer and orderItem collection via the customer id in Orders and orderItemCollection in Orders
        // generate basic invoice format with gets from Orders and Customer
        // for loop to get the productId for each orderItem, use this to get Product name(s), quantity, price and total
        // finally generate the footer and return the <ByteArrayResource> ResponseEntity


        Optional<Orders> optionalOrder = orderService.findById(orderId);
        if (optionalOrder.isEmpty()) {
            String errorMessage = messageSource.getMessage("order.notfound", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(errorMessage);
        }

        Orders order = optionalOrder.get();
        OrderStatus status = order.getOrderStatusId();
        List<OrderItem> items = order.getOrderItemCollection();

        try {

            // Check if the order status is either "pending" or "processing"
            if (!"pending".equals(status.getStatus().toLowerCase()) &&
                    !"processing".equals(status.getStatus().toLowerCase()) || items.isEmpty()) {
                throw new InvoiceErrorException();

            } else {

                Customer customer = order.getCustomerId();


                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(messageSource.getMessage("date.format", null, locale));
                String formattedDate = order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter);

                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    PdfWriter writer = new PdfWriter(outputStream);
                    PdfDocument pdf = new PdfDocument(writer);
                    Document document = new Document(pdf, PageSize.A4);

                    // Generate the invoice header
                    String invoiceTitle = messageSource.getMessage("invoice.title", null, locale);
                    Paragraph title = new Paragraph(invoiceTitle)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setBold()
                            .setFontSize(18);
                    document.add(title);



                    String invoiceDetails = String.format("%s: %d\n%s: %s\n%s: %s %s\n%s: %s\n\n",
                            messageSource.getMessage("invoice.orderId", null, locale), order.getOrderId(),
                            messageSource.getMessage("invoice.date", null, locale), formattedDate,
                            messageSource.getMessage("invoice.customer", null, locale), customer.getFirstName(), customer.getLastName(),
                            messageSource.getMessage("invoice.address", null, locale), customer.getAddress());


                    document.add(new Paragraph(invoiceDetails));

                    // Generate the invoice body for each order item
                    BigDecimal grandTotal = BigDecimal.ZERO;
                    for (OrderItem item : items) {
                        Product product = item.getProductId();
                        BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                        grandTotal = grandTotal.add(itemTotal);

                        String itemDetails = String.format("%s - %d x $%.2f = $%.2f\n",
                                product.getName(), item.getQuantity(), item.getPrice(), itemTotal);
                        document.add(new Paragraph(itemDetails));
                    }

                    // Generate the invoice footer with the grand total
                    String localisedTotal = messageSource.getMessage("invoice.total", null, locale);
                    Paragraph total = new Paragraph(String.format(localisedTotal + ": $%.2f", grandTotal))
                            .setBold();
                    document.add(total);

                    String message = messageSource.getMessage("invoice.thanks", null, locale);
                    Paragraph thanks = new Paragraph(message);
                    document.add(thanks);

                    document.close();

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + orderId + ".pdf");

                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(new ByteArrayResource(outputStream.toByteArray()));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            }
        } catch (InvoiceErrorException ex) {
            String errorMessage = messageSource.getMessage("invoice.error", null, locale);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(errorMessage);
        }
    }





    @PutMapping("")
    @ApiOperation(value = "Edit a customer", notes = "Updates the details of an existing customer.")
    @ApiResponses({
                @ApiResponse(code = 200, message = "Customer updated successfully"),
                @ApiResponse(code = 400, message = "Invalid request payload"),
                @ApiResponse(code = 404, message = "Customer not found")
        })
    public ResponseEntity edit(@RequestBody Customer c) {
        customerService.saveCustomer(c);
        return new ResponseEntity<>(HttpStatus.OK);
    }





}










