package com.cypr.security;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class HtmlSanitizer {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.LINKS)
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.IMAGES);

    /**
     * Sanitizes untrusted user input to prevent Stored XSS.
     * Allows basic formatting (b, i, u, etc.), links, blocks, and images.
     * 
     * @param input the untrusted input
     * @return the sanitized HTML
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return POLICY.sanitize(input);
    }
}
