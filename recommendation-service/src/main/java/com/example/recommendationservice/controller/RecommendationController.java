package com.example.recommendationservice.controller;

import com.example.recommendationservice.model.Recommendation;
import com.example.recommendationservice.repository.RecommendationRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    
    private final RecommendationRepository repo;
    
    public RecommendationController(RecommendationRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Recommendation create(@Valid @RequestBody Recommendation r) {
        return repo.save(r);
    }

    // ⭐ EXISTANT - avec path variable
    @GetMapping("/product/{productId}")
    public List<Recommendation> byProduct(@PathVariable Long productId) {
        return repo.findByProductId(productId);
    }

    // ⭐⭐ NOUVEAU - avec paramètre query (nécessaire pour composite service)
    @GetMapping
    public List<Recommendation> byProductQuery(@RequestParam Long productId) {
        return repo.findByProductId(productId);
    }

    // ✅ EXISTANT - get all
    @GetMapping("/all")
    public List<Recommendation> getAll() {
        return repo.findAll();
    }
}