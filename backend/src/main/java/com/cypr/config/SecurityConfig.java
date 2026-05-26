package com.cypr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. In-built CORS reference load karo jo filter chain se pehle chalega
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. CSRF ko stateless JWT ke liye disable rakho
                .csrf(csrf -> csrf.disable())

                // 3. Endpoints Rules: Dev mode ke liye sabhi paths completely open hain
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    // Standalone CorsConfigurationSource Bean jo preflight OPTIONS request ko direct handle karegi
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://cyprtech.vercel.app", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // OPTIONS explicitly zaroori hai!
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type")); // Headers explicit specify kiye
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // Preflight cache timing

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}