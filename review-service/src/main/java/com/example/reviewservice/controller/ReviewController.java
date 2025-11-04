package com.example.reviewservice.controller;

import com.example.reviewservice.model.Review;
import com.example.reviewservice.repository.ReviewRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    
    private final ReviewRepository repo;
    
    public ReviewController(ReviewRepository repo) {
        this.repo = repo;
    }

    // Créer un avis
    @PostMapping
    public Review create(@Valid @RequestBody Review r) {
        return repo.save(r);
    }

    // ⭐ EXISTANT - avec path variable
    @GetMapping("/product/{productId}")
    public List<Review> byProduct(@PathVariable Long productId) {
        return repo.findByProductId(productId);
    }

    // ⭐⭐ NOUVEAU - avec paramètre query (NÉCESSAIRE pour composite service)
    @GetMapping
    public List<Review> byProductQuery(@RequestParam Long productId) {
        return repo.findByProductId(productId);
    }

    // ✅ EXISTANT - get all (renommez pour éviter le conflit)
    @GetMapping("/all")
    public List<Review> getAllReviews() {
        return repo.findAll();
    }
}