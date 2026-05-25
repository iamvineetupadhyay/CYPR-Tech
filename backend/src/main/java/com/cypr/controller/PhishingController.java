package com.cypr.controller;

import com.cypr.engine.PhishingDetectionEngine;
import com.cypr.entity.ScanHistory;
import com.cypr.entity.User;
import com.cypr.repository.ScanRepository;
import com.cypr.repository.UserRepository;
import com.cypr.service.PhishingListService;
import com.cypr.service.VirusTotalService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/phish-check")
public class PhishingController {

    private final PhishingListService phishingListService;
    private final VirusTotalService virusTotalService;
    private final UserRepository userRepository;
    private final ScanRepository scanRepository;
    private final PhishingDetectionEngine detectionEngine = new PhishingDetectionEngine();

    public PhishingController(PhishingListService phishingListService,
                              VirusTotalService virusTotalService,
                              UserRepository userRepository,
                              ScanRepository scanRepository) {
        this.phishingListService = phishingListService;
        this.virusTotalService = virusTotalService;
        this.userRepository = userRepository;
        this.scanRepository = scanRepository;
    }

    @PostMapping
    public Map<String, Object> checkUrl(@RequestBody Map<String, Object> request) {
        String raw = Optional.ofNullable((String) request.get("url")).orElse("").trim();
        boolean isAdvanced = request.get("isAdvanced") != null && (boolean) request.get("isAdvanced");

        // --- STEP 1: User Fetch ---
        Long currentUserId = null;
        Object userIdObj = request.get("userId");
        if (userIdObj != null) {
            try {
                currentUserId = Long.valueOf(userIdObj.toString());
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        User user = null;
        if (currentUserId != null) {
            user = userRepository.findById(currentUserId).orElse(null);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("url", raw);

        // --- STEP 2: Real-time Feed Check ---
        String urlLower = raw.toLowerCase();
        boolean reportedInFeed = phishingListService.getRealTimeList() != null &&
                phishingListService.getRealTimeList().stream().anyMatch(urlLower::contains);

        // --- STEP 3: VAJRA Engine Analysis (Free & Unlimited) ---
        PhishingDetectionEngine.EngineResult engineResult = detectionEngine.analyze(raw);

        // --- STEP 4: Advanced Scan Logic (Conditional Restriction) ---
        Map<String, Object> vtResult = null;
        boolean vtMalicious = false;

        if (isAdvanced) {
            if (user != null && user.getCredits() <= 0) {
                result.put("status", "Error: Daily Credit Limit Reached for Advanced Scan!");
                result.put("reasons", List.of("You can still use Local Scan for free. Turn off Advanced Scan to continue."));
                return result;
            }

            if (!reportedInFeed && virusTotalService.isEnabled()) {
                vtResult = virusTotalService.scanUrl(raw);
                vtMalicious = vtResult != null &&
                        vtResult.getOrDefault("Verdict", "").toString().equalsIgnoreCase("malicious");

                if (user != null) {
                    user.setCredits(user.getCredits() - 1);
                    userRepository.save(user);
                }
            }
        }

        // --- STEP 5: Final Status Determination ---
        String status;
        String dbResult;
        if (reportedInFeed) { status = "Reported phishing (Global Feed)"; dbResult = "Risky"; }
        else if (vtMalicious) { status = "VirusTotal: Flagged as Malicious"; dbResult = "Risky"; }
        else if (!engineResult.riskTier().equals("LOW")) { status = engineResult.summary(); dbResult = "Risky"; }
        else { status = "Secure (Verified by VAJRA Engine)"; dbResult = "Secure"; }

        // --- STEP 6: Save History ---
        if (user != null) {
            ScanHistory history = new ScanHistory();
            history.setUser(user);
            history.setUrl(raw);
            history.setResult(dbResult);
            scanRepository.save(history);

            // Recompute user's safetyScore dynamically based on their scans
            List<ScanHistory> userHistory = scanRepository.findByUserIdOrderByTimestampDesc(user.getId());
            int score = 100;
            for (ScanHistory sh : userHistory) {
                if ("Risky".equalsIgnoreCase(sh.getResult())) {
                    score -= 15;
                }
            }
            if (score < 10) score = 10;
            if (score > 100) score = 100;
            user.setSafetyScore(score);
            userRepository.save(user);
        }

        // --- STEP 7: Final Response JSON ---
        result.put("status", status);
        result.put("riskTier", engineResult.riskTier());
        result.put("riskScore", engineResult.normalizedScore());
        if (vtResult != null) result.put("virusTotal", vtResult);
        if (!engineResult.reasons().isEmpty()) result.put("reasons", engineResult.reasons());

        return result;
    }
}