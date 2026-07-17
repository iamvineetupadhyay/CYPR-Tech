package com.cypr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AntiAbuseService {

    private static final Logger log = LoggerFactory.getLogger(AntiAbuseService.class);

    // IP -> block expiry timestamp (ms)
    private final Cache<String, Long> blockedIps = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build();

    // IP/Username -> failed attempts count
    private final Cache<String, Integer> failedAttempts = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    // Email -> last email requested timestamp (ms)
    private final Cache<String, Long> lastEmailSentTime = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    // Email/IP -> hourly count
    private final Cache<String, ConcurrentHashMap<Integer, Integer>> hourlyEmailRequestCount = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(2, TimeUnit.HOURS)
            .build();

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long TEMP_BLOCK_DURATION_MS = 15 * 60 * 1000; // 15 minutes
    private static final long EMAIL_COOLDOWN_MS = 60 * 1000; // 60 seconds between resends
    private static final int MAX_EMAILS_PER_HOUR = 5;

    // ── IP BLOCKING & LOGIN ATTEMPTS ──────────────────────────────────────────

    public boolean isIpBlocked(String ip) {
        if (ip == null) return false;
        Long expiry = blockedIps.getIfPresent(ip);
        if (expiry == null) return false;

        if (System.currentTimeMillis() > expiry) {
            blockedIps.invalidate(ip); // Block expired
            failedAttempts.invalidate(ip);
            log.info("[AntiAbuseService] Temporary block expired and removed for IP: {}", ip);
            return false;
        }
        return true;
    }

    public void registerFailedAttempt(String ip, String username) {
        if (ip != null) {
            int ipAttempts = failedAttempts.asMap().merge(ip, 1, Integer::sum);
            log.warn("[AntiAbuseService] Failed attempt registered for IP: {}. Current failed attempts: {}", ip, ipAttempts);
            if (ipAttempts >= MAX_FAILED_ATTEMPTS) {
                long blockUntil = System.currentTimeMillis() + TEMP_BLOCK_DURATION_MS;
                blockedIps.put(ip, blockUntil);
                log.error("[AntiAbuseService] Maximum failed attempts reached. IP {} temporarily blocked for 15 minutes.", ip);
            }
        }
        if (username != null) {
            failedAttempts.asMap().merge(username, 1, Integer::sum);
        }
    }

    public void registerSuccess(String ip, String username) {
        if (ip != null) {
            failedAttempts.invalidate(ip);
            blockedIps.invalidate(ip);
        }
        if (username != null) {
            failedAttempts.invalidate(username);
        }
    }

    public boolean shouldRequireCaptcha(String username) {
        if (username == null) return false;
        Integer attempts = failedAttempts.getIfPresent(username);
        return attempts != null && attempts >= 3;
    }

    // ── EMAIL RATE LIMITS & COOLDOWN ──────────────────────────────────────────

    public boolean checkEmailCooldown(String email) {
        if (email == null) return true;
        Long lastSent = lastEmailSentTime.getIfPresent(email);
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
        
        ConcurrentHashMap<Integer, Integer> hourlyMap = hourlyEmailRequestCount.get(email, k -> new ConcurrentHashMap<>());
        
        // Clean up previous hours
        hourlyMap.keySet().removeIf(hour -> hour < currentHour);

        Integer count = hourlyMap.getOrDefault(currentHour, 0);
        return count >= MAX_EMAILS_PER_HOUR;
    }

    private void incrementHourlyCount(String email) {
        int currentHour = (int) (System.currentTimeMillis() / (3600 * 1000));
        ConcurrentHashMap<Integer, Integer> hourlyMap = hourlyEmailRequestCount.get(email, k -> new ConcurrentHashMap<>());
        hourlyMap.merge(currentHour, 1, Integer::sum);
    }

    public long getRemainingCooldownSeconds(String email) {
        if (email == null) return 0;
        Long lastSent = lastEmailSentTime.getIfPresent(email);
        if (lastSent == null) return 0;

        long elapsedMs = System.currentTimeMillis() - lastSent;
        long remainingMs = EMAIL_COOLDOWN_MS - elapsedMs;
        return remainingMs <= 0 ? 0 : (remainingMs / 1000);
    }
}
