package com.example.productservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.productservice.model.Product;
public interface ProductRepository extends JpaRepository<Product, Long> {}
