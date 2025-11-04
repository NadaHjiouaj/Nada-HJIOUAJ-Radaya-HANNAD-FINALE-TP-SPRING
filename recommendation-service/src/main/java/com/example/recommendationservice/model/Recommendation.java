package com.example.recommendationservice.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Entity
public class Recommendation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    @NotBlank
    private String author;
    @Min(0) @Max(100)
    private int rate;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public Long getProductId(){return productId;}
    public void setProductId(Long productId){this.productId=productId;}
    public String getAuthor(){return author;}
    public void setAuthor(String author){this.author=author;}
    public int getRate(){return rate;}
    public void setRate(int rate){this.rate=rate;}
}
