package com.cypr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS setting: Frontend ko allow karo
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("https://cyprtech.vercel.app", "http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                // 2. CSRF ko abhi stateless JWT ke liye disable kar rahe hain
                .csrf(csrf -> csrf.disable())
                // 3. Endpoints Rules: Kaun andar aayega, kaun block hoga
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/verify", "/api/user/login", "/api/user/resend-verification", "/api/user/forgot-password", "/api/user/reset-password").permitAll() // 🔥 Custom endpoints open hain
                        .anyRequest().authenticated() // 🔒 Baaki saare analytics aur dashboard endpoints locked hain!
                );

        return http.build();
    }
}