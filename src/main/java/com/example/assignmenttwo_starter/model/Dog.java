package com.example.assignmenttwo_starter.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "dogs")
public class Dog extends RepresentationModel<Dog> implements Serializable {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "vaccinated")
    private boolean vaccinated;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    //  list of dogs currently registered for the upcoming dog show (as defined by what date user inputs)
    @OneToMany(mappedBy = "dog", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Exclude
    private List<DogShowRegistration> registrations;

    @OneToMany(mappedBy = "dog", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Exclude
    private List<ImageData> images = new ArrayList<>();


    public void addImage(ImageData imageData) {
        images.add(imageData);
        imageData.setDog(this);
    }

    public void removeImage(ImageData imageData) {
        images.remove(imageData);
        imageData.setDog(null);
    }
}
