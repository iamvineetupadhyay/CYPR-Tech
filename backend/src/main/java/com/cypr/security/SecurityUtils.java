package com.cypr.security;

import jakarta.servlet.http.HttpServletRequest;

public class SecurityUtils {

    /**
     * Retrieves the authenticated user's ID from the request context.
     * This attribute is set securely by JwtAuthenticationFilter upon successful token validation.
     *
     * @param request The current HTTP request.
     * @return The authenticated user's ID, or null if not authenticated.
     */
    public static Long getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("authenticatedUserId");
        if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        return null;
    }
}
