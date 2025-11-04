package com.example.authservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class AuthController {
    
    @Value("${security.admin.username:admin}")
    private String adminUser;
    
    @Value("${security.admin.password:adminpass}")
    private String adminPass;
    
    @Value("${security.user.username:user}")
    private String userUser;
    
    @Value("${security.user.password:userpass}")
    private String userPass;

    @GetMapping("/auth/check")
    public ResponseEntity<Map<String, Object>> check(@RequestParam(value="username", required=false) String username,
                                                     @RequestParam(value="password", required=false) String password,
                                                     @RequestParam(value="role", required=false) String role) {
        boolean valid = false;
        String userRole = "";
        String message = "";
        
        if(username != null && password != null){
            if(username.equals(adminUser) && password.equals(adminPass) && "ADMIN".equals(role)) {
                valid = true;
                userRole = "ADMIN";
                message = "Authentification administrateur réussie";
            }
            if(username.equals(userUser) && password.equals(userPass) && "USER".equals(role)) {
                valid = true;
                userRole = "USER";
                message = "Authentification utilisateur réussie";
            }
        }
        
        if(valid) {
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", username,
                "role", userRole,
                "message", message,
                "timestamp", java.time.LocalDateTime.now(),
                "permissions", getPermissions(userRole)
            ));
        } else {
            return ResponseEntity.status(401).body(Map.of(
                "valid", false,
                "message", "Échec de l'authentification - Vérifiez vos identifiants et rôle",
                "timestamp", java.time.LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/auth/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestParam(value="username", required=false) String username,
                                                      @RequestParam(value="password", required=false) String password,
                                                      @RequestParam(value="role", required=false) String role) {
        // La même logique que check()
        return check(username, password, role);
    }

    // Méthode pour obtenir les permissions selon le rôle
    private Map<String, Boolean> getPermissions(String role) {
        if ("ADMIN".equals(role)) {
            return Map.of(
                "can_read", true,
                "can_write", true,
                "can_delete", true,
                "can_manage_users", true
            );
        } else if ("USER".equals(role)) {
            return Map.of(
                "can_read", true,
                "can_write", false,
                "can_delete", false,
                "can_manage_users", false
            );
        } else {
            return Map.of(
                "can_read", false,
                "can_write", false,
                "can_delete", false,
                "can_manage_users", false
            );
        }
    }
}