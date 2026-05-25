package com.cypr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AntiAbuseService {

    private static final Logger log = LoggerFactory.getLogger(AntiAbuseService.class);

    // IP -> block expiry timestamp (ms)
    private final ConcurrentHashMap<String, Long> blockedIps = new ConcurrentHashMap<>();

    // IP/Username -> failed attempts count
    private final ConcurrentHashMap<String, Integer> failedAttempts = new ConcurrentHashMap<>();

    // Email -> last email requested timestamp (ms)
    private final ConcurrentHashMap<String, Long> lastEmailSentTime = new ConcurrentHashMap<>();

    // Email/IP -> hourly count
    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Integer>> hourlyEmailRequestCount = new ConcurrentHashMap<>();

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long TEMP_BLOCK_DURATION_MS = 15 * 60 * 1000; // 15 minutes
    private static final long EMAIL_COOLDOWN_MS = 60 * 1000; // 60 seconds between resends
    private static final int MAX_EMAILS_PER_HOUR = 5;

    // ── IP BLOCKING & LOGIN ATTEMPTS ──────────────────────────────────────────

    public boolean isIpBlocked(String ip) {
        if (ip == null) return false;
        Long expiry = blockedIps.get(ip);
        if (expiry == null) return false;

        if (System.currentTimeMillis() > expiry) {
            blockedIps.remove(ip); // Block expired
            failedAttempts.remove(ip);
            log.info("[AntiAbuseService] Temporary block expired and removed for IP: {}", ip);
            return false;
        }
        return true;
    }

    public void registerFailedAttempt(String ip, String username) {
        if (ip != null) {
            int ipAttempts = failedAttempts.merge(ip, 1, Integer::sum);
            log.warn("[AntiAbuseService] Failed attempt registered for IP: {}. Current failed attempts: {}", ip, ipAttempts);
            if (ipAttempts >= MAX_FAILED_ATTEMPTS) {
                long blockUntil = System.currentTimeMillis() + TEMP_BLOCK_DURATION_MS;
                blockedIps.put(ip, blockUntil);
                log.error("[AntiAbuseService] Maximum failed attempts reached. IP {} temporarily blocked for 15 minutes.", ip);
            }
        }
        if (username != null) {
            failedAttempts.merge(username, 1, Integer::sum);
        }
    }

    public void registerSuccess(String ip, String username) {
        if (ip != null) {
            failedAttempts.remove(ip);
            blockedIps.remove(ip);
        }
        if (username != null) {
            failedAttempts.remove(username);
        }
    }

    public boolean shouldRequireCaptcha(String username) {
        if (username == null) return false;
        Integer attempts = failedAttempts.get(username);
        return attempts != null && attempts >= 3;
    }

    // ── EMAIL RATE LIMITS & COOLDOWN ──────────────────────────────────────────

    public boolean checkEmailCooldown(String email) {
        if (email == null) return true;
        Long lastSent = lastEmailSentTime.get(email);
        if (lastSent == null) return true;

        long elapsed = System.currentTimeMillis() - lastSent;
        return elapsed >= EMAIL_COOLDOWN_MS;
    }

    public void recordEmailSent(String email) {
        if (email == null) return;
        lastEmailSentTime.put(email, System.currentTimeMillis());
        incrementHourlyCount(email);
    }

    public boolean isEmailLimitExceeded(String email) {
        if (email == null) return false;
        int currentHour = (int) (System.currentTimeMillis() / (3600 * 1000));
        
        ConcurrentHashMap<Integer, Integer> hourlyMap = hourlyEmailRequestCount.computeIfAbsent(email, k -> new ConcurrentHashMap<>());
        
        // Clean up previous hours
        hourlyMap.keySet().removeIf(hour -> hour < currentHour);

        Integer count = hourlyMap.getOrDefault(currentHour, 0);
        return count >= MAX_EMAILS_PER_HOUR;
    }

    private void incrementHourlyCount(String email) {
        int currentHour = (int) (System.currentTimeMillis() / (3600 * 1000));
        ConcurrentHashMap<Integer, Integer> hourlyMap = hourlyEmailRequestCount.computeIfAbsent(email, k -> new ConcurrentHashMap<>());
        hourlyMap.merge(currentHour, 1, Integer::sum);
    }

    public long getRemainingCooldownSeconds(String email) {
        if (email == null) return 0;
        Long lastSent = lastEmailSentTime.get(email);
        if (lastSent == null) return 0;

        long elapsedMs = System.currentTimeMillis() - lastSent;
        long remainingMs = EMAIL_COOLDOWN_MS - elapsedMs;
        return remainingMs <= 0 ? 0 : (remainingMs / 1000);
    }
}
