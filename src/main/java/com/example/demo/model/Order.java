package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String email;
    private String address;
    private String state;
    private String city;
    private String zip;
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Cart cart;
    @JsonIgnore
    @OneToOne(cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
    private Payment payment;
}
