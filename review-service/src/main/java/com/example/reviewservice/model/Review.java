package com.example.reviewservice.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Entity
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    @NotBlank
    private String author;
    private String subject;
    private String content;
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public Long getProductId(){return productId;}
    public void setProductId(Long productId){this.productId=productId;}
    public String getAuthor(){return author;}
    public void setAuthor(String author){this.author=author;}
    public String getSubject(){return subject;}
    public void setSubject(String subject){this.subject=subject;}
    public String getContent(){return content;}
    public void setContent(String content){this.content=content;}
}
