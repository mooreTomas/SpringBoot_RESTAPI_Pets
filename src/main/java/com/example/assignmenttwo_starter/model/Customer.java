package com.example.assignmenttwo_starter.model;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import com.example.assignmenttwo_starter.model.Dog;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "customers")
public class Customer extends RepresentationModel<Customer> implements Serializable{




    @Id
    @NotNull
    @Column(name = "customer_id")
    private Integer customerId;

    @Size(max = 50)
    @Column(name = "first_name")
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name")
    private String lastName;

    @Email(message = "Invalid email")
    @NotNull
    @Size(min = 5, max = 255, message = "email is required and must be in correct format!!!")
    @Column(name = "email")
    private String email;

    @Pattern(regexp = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message = "Invalid phone format, should be as xxx-xxx-xxxx")
    // accepts these kinds of numbers: (123) 456-7890
    // (123)456-7890
    // 123-456-7890
    // 123 456 7890
    // 1234567890
    @Size(max = 20)
    @Column(name = "phone")
    private String phone;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Size(max = 50)
    @Column(name = "city")
    private String city;

    @Size(max = 50)
    @Column(name = "country")
    private String country;

    @Size(max = 10)
    @Column(name = "postcode")
    private String postcode;

    @OneToMany(mappedBy = "customerId")
    @JsonManagedReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Exclude
    private List<Review> reviewCollection;

    @OneToMany(mappedBy = "customerId")
    @JsonManagedReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Exclude
    private List<Orders> ordersCollection;


    // list of dogs the customer owns
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"customer"})
    @ToString.Exclude
    private List<Dog> dogs;
}
