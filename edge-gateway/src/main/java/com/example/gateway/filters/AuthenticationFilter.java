package com.example.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;

    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8090").build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("=== AUTHENTICATION FILTER EXECUTED ===");
        
        // Récupérer les headers d'authentification
        String username = exchange.getRequest().getHeaders().getFirst("username");
        String password = exchange.getRequest().getHeaders().getFirst("password");
        String role = exchange.getRequest().getHeaders().getFirst("role");
        
        System.out.println("Authentication attempt - Username: " + username + ", Role: " + role);
        
        // Vérifier si les headers d'authentification sont présents
        if (username == null || password == null || role == null) {
            System.out.println("Missing authentication headers - Blocking request");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        // Appeler le service d'authentification
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path("/auth/verify")
                    .queryParam("username", username)
                    .queryParam("password", password)
                    .queryParam("role", role)
                    .build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(authResponse -> {
                    Boolean isValid = (Boolean) authResponse.get("valid");
                    System.out.println("Auth service response - Valid: " + isValid);
                    
                    if (Boolean.TRUE.equals(isValid)) {
                        // ✅ RÉCUPÉRER LES PERMISSIONS
                        Map<String, Boolean> permissions = (Map<String, Boolean>) authResponse.get("permissions");
                        String userRole = (String) authResponse.get("role");
                        
                        System.out.println("User role: " + userRole);
                        System.out.println("Permissions: " + permissions);
                        
                        // 🔒 VÉRIFIER LES AUTORISATIONS SELON LA MÉTHODE HTTP
                        String method = exchange.getRequest().getMethod().name();
                        boolean isAuthorized = checkAuthorization(method, permissions);
                        
                        if (isAuthorized) {
                            System.out.println("Authorization GRANTED for " + method + " - Proceeding to service");
                            // Authentication ET autorisation réussies, continuer
                            return chain.filter(exchange);
                        } else {
                            System.out.println("Authorization DENIED for " + method + " - User lacks permissions");
                            // Authentification réussie mais autorisation refusée
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }
                    } else {
                        System.out.println("Authentication FAILED - Blocking request");
                        // Authentication échouée, bloquer
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                })
                .onErrorResume(error -> {
                    System.out.println("Error calling auth service: " + error.getMessage());
                    // En cas d'erreur, bloquer la requête
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }

    // 🔒 MÉTHODE POUR VÉRIFIER LES AUTORISATIONS
    private boolean checkAuthorization(String httpMethod, Map<String, Boolean> permissions) {
        if (permissions == null) {
            return false;
        }
        
        switch (httpMethod) {
            case "GET":
                return Boolean.TRUE.equals(permissions.get("can_read"));
            case "POST":
            case "PUT":
                return Boolean.TRUE.equals(permissions.get("can_write"));
            case "DELETE":
                return Boolean.TRUE.equals(permissions.get("can_delete"));
            default:
                return false;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // Exécuter ce filtre en premier
    }
}