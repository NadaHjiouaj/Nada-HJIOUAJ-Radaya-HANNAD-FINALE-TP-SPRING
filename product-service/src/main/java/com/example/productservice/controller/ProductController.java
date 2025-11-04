package com.example.productservice.controller;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository repo;
    public ProductController(ProductRepository repo){this.repo=repo;}
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product p){
        return new ResponseEntity<>(repo.save(p), HttpStatus.CREATED);
    }
    @GetMapping
    public List<Product> all(){return repo.findAll();}
    @GetMapping("/{id}")
    public Product getOne(@PathVariable Long id){return repo.findById(id).orElseThrow();}
}
