package com.example.productservice.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    @Min(0)
    @Max(100)
    private int weight;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getName(){return name;}
    public void setName(String name){this.name=name;}
    public int getWeight(){return weight;}
    public void setWeight(int weight){this.weight=weight;}
}
