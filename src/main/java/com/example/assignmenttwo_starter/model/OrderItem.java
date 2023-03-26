package com.example.assignmenttwo_starter.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "order_items")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "orderItemId")
public class OrderItem  implements Serializable {

    @Id
    @NotNull
    @Column(name = "order_item_id")
    private Integer orderItemId;

    @Column(name = "quantity")
    private Integer quantity;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price")
    private BigDecimal price;

    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    @ManyToOne
    @JsonBackReference
    @ToString.Exclude
    private Orders orderId;

    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    @ManyToOne
    @JsonManagedReference
    @ToString.Exclude
    private Product productId;

}
