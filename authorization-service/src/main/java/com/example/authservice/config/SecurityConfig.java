package com.example.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Désactive CSRF pour les APIs REST
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/check", "/auth/verify").permitAll()  // Permet l'accès sans authentification
                .anyRequest().authenticated()  // Toutes les autres requêtes nécessitent une authentification
            )
            .httpBasic(httpBasic -> {});  // Active l'authentification Basic Auth pour les autres endpoints
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Création de l'utilisateur ADMIN
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("adminpass"))  // Mot de passe crypté
            .roles("ADMIN")  // Rôle ADMIN
            .build();
            
        // Création de l'utilisateur USER  
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("userpass"))    // Mot de passe crypté
            .roles("USER")    // Rôle USER
            .build();
            
        // Retourne le gestionnaire d'utilisateurs avec les 2 comptes
        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Encodeur de mots de passe BCrypt
        return new BCryptPasswordEncoder();
    }
}