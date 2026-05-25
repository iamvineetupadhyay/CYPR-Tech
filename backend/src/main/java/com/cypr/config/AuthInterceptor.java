package com.cypr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow OPTIONS requests for CORS pre-flight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized: Missing or invalid Authorization header.\"}");
            return false;
        }

        String token = authHeader.substring(7);
        Long tokenUserId = jwtUtil.validateTokenAndGetUserId(token);

        if (tokenUserId == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized: Session has expired or is invalid.\"}");
            return false;
        }

        // BOLA / IDOR Verification: Compare path variables (id or userId) with token's userId claim
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables != null) {
            String pathIdStr = pathVariables.get("id");
            if (pathIdStr == null) {
                pathIdStr = pathVariables.get("userId");
            }

            if (pathIdStr != null) {
                try {
                    Long pathUserId = Long.valueOf(pathIdStr);
                    if (!tokenUserId.equals(pathUserId)) {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Forbidden: Access denied to other user's resources.\"}");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    // Keep proceeding if pathIdStr is not a numerical ID
                }
            }
        }

        // Inject authenticated ID as a request attribute for controllers to use if needed
        request.setAttribute("authenticatedUserId", tokenUserId);
        return true;
    }
}
