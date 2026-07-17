package com.cypr.security;

import com.cypr.config.JwtUtil;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import com.cypr.modules.security.entity.Session;
import com.cypr.modules.security.repository.SessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService,
            SessionRepository sessionRepository,
            UserRepository userRepository
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtUtil.extractEmail(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtUtil.isTokenValid(jwt)) {
                // Check if session exists in DB
                java.util.Optional<Session> sessionOpt = sessionRepository.findByToken(jwt);
                Session session = null;
                boolean shouldAuthenticate = false;

                if (sessionOpt.isPresent()) {
                    session = sessionOpt.get();
                    if (session.isActive() && session.getExpiresAt().isAfter(LocalDateTime.now())) {
                        shouldAuthenticate = true;
                    }
                } else {
                    // Auto-create session if cryptographically valid but not yet in DB
                    Long userId = jwtUtil.extractUserId(jwt);
                    if (userId != null) {
                        java.util.Optional<User> userOpt = userRepository.findById(userId);
                        if (userOpt.isPresent()) {
                            String userAgent = request.getHeader("User-Agent");
                            String ip = request.getHeader("X-Forwarded-For");
                            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                                ip = request.getRemoteAddr();
                            }
                            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                                ip = "127.0.0.1";
                            }

                            session = new Session();
                            session.setUser(userOpt.get());
                            session.setToken(jwt);
                            session.setExpiresAt(LocalDateTime.now().plusHours(24));
                            session.setActive(true);
                            session.setIpAddress(ip);
                            session.setUserAgent(userAgent);
                            session.setLastActivityAt(LocalDateTime.now());
                            
                            // Client type: e.g. parse simple browser name from User-Agent
                            String clientType = "Daemon Client";
                            if (userAgent != null) {
                                if (userAgent.contains("Mozilla") || userAgent.contains("Chrome") || userAgent.contains("Safari") || userAgent.contains("Firefox")) {
                                    clientType = "Web Console";
                                }
                            }
                            session.setClientType(clientType);
                            session.setLocation("Delhi, India"); // Or standard location
                            
                            try {
                                session = sessionRepository.saveAndFlush(session);
                            } catch (org.springframework.dao.DataIntegrityViolationException | org.hibernate.exception.ConstraintViolationException e) {
                                session = sessionRepository.findByToken(jwt).orElse(null);
                            } catch (Exception e) {
                                session = sessionRepository.findByToken(jwt).orElse(null);
                            }
                            if (session != null) {
                                shouldAuthenticate = true;
                            }
                        }
                    }
                }

                if (shouldAuthenticate && session != null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    Long userId = jwtUtil.extractUserId(jwt);
                    request.setAttribute("authenticatedUserId", userId);
                    request.setAttribute("authenticatedSessionId", session.getId().toString());
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
