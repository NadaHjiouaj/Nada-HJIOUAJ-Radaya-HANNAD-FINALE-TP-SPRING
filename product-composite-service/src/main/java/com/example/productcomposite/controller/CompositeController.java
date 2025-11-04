package com.example.productcomposite.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

// ⭐⭐ IMPORTS POUR CIRCUIT BREAKER ⭐⭐
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;

@RestController
@RequestMapping("/product-composite")
public class CompositeController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    // ⭐⭐ CIRCUIT BREAKER FACTORY ⭐⭐
    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    // ✅ ENDPOINT GET AVEC CIRCUIT BREAKER POUR TOUS LES SERVICES
    @GetMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> getComposite(@PathVariable Long id) {
        
        System.out.println("🔍 Début de getComposite pour product ID: " + id);
        
        // ⭐⭐ CIRCUIT BREAKERS POUR CHAQUE SERVICE ⭐⭐
        CircuitBreaker productCircuitBreaker = circuitBreakerFactory.create("productService");
        CircuitBreaker reviewCircuitBreaker = circuitBreakerFactory.create("reviewService");
        CircuitBreaker recommendationCircuitBreaker = circuitBreakerFactory.create("recommendationService");
        
        // ⭐⭐ APPEL PROTÉGÉ AU SERVICE PRODUCT ⭐⭐
        Map<String, Object> product = productCircuitBreaker.run(
            () -> {
                System.out.println("🟢 Appel à product-service pour ID: " + id);
                String productUrl = "http://product-service/products/" + id;
                Map<String, Object> result = restTemplate.getForObject(productUrl, Map.class);
                System.out.println("✅ Product-service a répondu: " + result);
                return result;
            },
            throwable -> {
                System.out.println("🔴 Fallback product-service activé: " + throwable.getMessage());
                return getProductFallback(id, throwable);
            }
        );
        
        // ⭐⭐ APPEL PROTÉGÉ AU SERVICE REVIEW ⭐⭐
        List<Map<String, Object>> reviews = reviewCircuitBreaker.run(
            () -> {
                System.out.println("🟢 Appel à review-service pour product ID: " + id);
                try {
                    // ⭐⭐ URL CORRIGÉE ⭐⭐
                    String reviewUrl = "http://review-service/reviews?productId=" + id;
                    List<Map<String, Object>> result = restTemplate.getForObject(reviewUrl, List.class);
                    System.out.println("✅ Review-service a répondu: " + (result != null ? result.size() : "null") + " reviews");
                    return result != null ? result : new ArrayList<>();
                } catch (Exception e) {
                    System.out.println("⚠️ Aucune review trouvée, retour liste vide");
                    return new ArrayList<>();
                }
            },
            throwable -> {
                System.out.println("🔴 Fallback review-service activé: " + throwable.getMessage());
                return getReviewsFallback(id, throwable);
            }
        );
        
        // ⭐⭐ APPEL PROTÉGÉ AU SERVICE RECOMMENDATION ⭐⭐
        List<Map<String, Object>> recommendations = recommendationCircuitBreaker.run(
            () -> {
                System.out.println("🟢 Appel à recommendation-service pour product ID: " + id);
                try {
                    // ⭐⭐ URL CORRIGÉE ⭐⭐
                    String recommendationUrl = "http://recommendation-service/recommendations?productId=" + id;
                    List<Map<String, Object>> result = restTemplate.getForObject(recommendationUrl, List.class);
                    System.out.println("✅ Recommendation-service a répondu: " + (result != null ? result.size() : "null") + " recommendations");
                    return result != null ? result : new ArrayList<>();
                } catch (Exception e) {
                    System.out.println("⚠️ Aucune recommendation trouvée, retour liste vide");
                    return new ArrayList<>();
                }
            },
            throwable -> {
                System.out.println("🔴 Fallback recommendation-service activé: " + throwable.getMessage());
                return getRecommendationsFallback(id, throwable);
            }
        );
        
        // ⭐⭐ CONSTRUCTION DE LA RÉPONSE ⭐⭐
        Map<String, Object> response = new HashMap<>();
        response.put("product", product);
        response.put("reviews", reviews);
        response.put("recommendations", recommendations);
        
        System.out.println("🎯 Réponse composite construite avec succès");
        return ResponseEntity.ok(response);
    }

    // ⭐⭐ MÉTHODES FALLBACK AMÉLIORÉES ⭐⭐
    private Map<String, Object> getProductFallback(Long productId, Throwable throwable) {
        System.out.println("🚨 Circuit Breaker ACTIVÉ pour product-service - Product ID: " + productId);
        
        Map<String, Object> fallbackProduct = new HashMap<>();
        fallbackProduct.put("id", productId);
        fallbackProduct.put("name", "Produit temporairement indisponible");
        fallbackProduct.put("weight", 0);
        fallbackProduct.put("fallback", true);
        fallbackProduct.put("message", "Service produit momentanément indisponible");
        
        return fallbackProduct;
    }

    private List<Map<String, Object>> getReviewsFallback(Long productId, Throwable throwable) {
        System.out.println("🚨 Circuit Breaker ACTIVÉ pour review-service - Product ID: " + productId);
        return new ArrayList<>();
    }

    private List<Map<String, Object>> getRecommendationsFallback(Long productId, Throwable throwable) {
        System.out.println("🚨 Circuit Breaker ACTIVÉ pour recommendation-service - Product ID: " + productId);
        return new ArrayList<>();
    }

    // ✅ ENDPOINT POST (CRÉATION DE PRODUIT)
    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Map<String, Object> product) {
        try {
            // Validation du poids
            Double weight = Double.parseDouble(product.get("weight").toString());
            if (weight < 0 || weight > 100) {
                return ResponseEntity.badRequest().body(Map.of("error", "Weight must be between 0 and 100kg"));
            }
            
            // Appel au service product pour créer le produit
            String productUrl = "http://product-service/products";
            Map createdProduct = restTemplate.postForObject(productUrl, product, Map.class);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create product: " + e.getMessage()));
        }
    }

    // ✅ ENDPOINT DE TEST
    @GetMapping("/test")
    public String test() {
        return "Composite Service with Circuit Breaker is working! ✅";
    }
}