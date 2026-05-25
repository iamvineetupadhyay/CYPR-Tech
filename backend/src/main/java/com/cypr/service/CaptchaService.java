package com.cypr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class CaptchaService {

    private static final Logger log = LoggerFactory.getLogger(CaptchaService.class);

    private final RestTemplate rest = new RestTemplate();

    @Value("${turnstile.secret.key:}")
    private String secretKey;

    @Value("${turnstile.enabled:false}")
    private boolean enabled;

    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    public boolean verifyToken(String token, String remoteIp) {
        if (!enabled || secretKey == null || secretKey.isBlank()) {
            log.info("[CaptchaService] CAPTCHA Turnstile is disabled or secret key is missing. Skipping verification.");
            return true;
        }

        if (token == null || token.isBlank()) {
            log.warn("[CaptchaService] Validation failed: token is blank.");
            return false;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("secret", secretKey);
            body.add("response", token);
            if (remoteIp != null && !remoteIp.isBlank()) {
                body.add("remoteip", remoteIp);
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = rest.postForEntity(VERIFY_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> respBody = response.getBody();
                Boolean success = (Boolean) respBody.get("success");
                if (Boolean.TRUE.equals(success)) {
                    log.info("[CaptchaService] Token verification successful.");
                    return true;
                } else {
                    log.warn("[CaptchaService] Token verification failed. Response body: {}", respBody);
                }
            }
        } catch (Exception e) {
            log.error("[CaptchaService] Error verifying Turnstile CAPTCHA token: {}", e.getMessage());
        }

        return false;
    }
}
