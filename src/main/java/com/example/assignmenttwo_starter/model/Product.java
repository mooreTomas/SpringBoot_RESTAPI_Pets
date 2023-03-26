package com.example.assignmenttwo_starter.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

// stack overflow error issue, see below for more, this addresses it
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

// addresses stack overflow issuer
// tells Jackson to use specified ID to uniquely identify the productId
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "productId")
public class Product  implements Serializable {

    @Id
    @NotNull
    @Column(name = "product_id")
    private Integer productId;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price")
    private BigDecimal price;

    @Size(max = 255)
    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "productId")
    @JsonManagedReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Exclude
    private List<Review> reviewCollection;

    @OneToMany(mappedBy = "productId")
    @JsonManagedReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Exclude
    private List<OrderItem> orderItemCollection;

    @JoinColumn(name = "categogy_id", referencedColumnName = "category_id")
    @ManyToOne(optional = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonBackReference
    @ToString.Exclude
    private Category categoryId;

}
