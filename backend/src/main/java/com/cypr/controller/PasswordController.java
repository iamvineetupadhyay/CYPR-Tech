package com.cypr.controller;

import com.cypr.entity.ScanHistory;
import com.cypr.entity.User;
import com.cypr.repository.ScanRepository;
import com.cypr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/password-check")
public class PasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScanRepository scanRepository;

    @PostMapping
    public Map<String, Object> checkPassword(@RequestBody Map<String, Object> body, jakarta.servlet.http.HttpServletRequest request) {
        String password = Optional.ofNullable((String) body.get("password")).orElse("");

        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()].*")) score++;

        String status =
                (score <= 2) ? "Weak" :
                (score == 3) ? "Medium" :
                (score == 4) ? "Strong" :
                "Very Strong";

        String dbResult = (score >= 4) ? "Secure" : "Risky";

        // --- STEP 1: User Fetch ---
        Long currentUserId = com.cypr.security.SecurityUtils.getCurrentUserId(request);
        User user = null;
        if (currentUserId != null) {
            user = userRepository.findById(currentUserId).orElse(null);
        }

        // --- STEP 2: Mask and Save to Scan History ---
        if (user != null) {
            StringBuilder stars = new StringBuilder();
            int len = Math.max(8, password.length());
            for (int i = 0; i < len; i++) {
                stars.append("*");
            }
            String maskedPasswordLog = "Password Check: " + stars.toString();

            ScanHistory history = new ScanHistory();
            history.setUser(user);
            history.setUrl(maskedPasswordLog);
            history.setResult(dbResult);
            scanRepository.save(history);

            // Recompute user's safetyScore dynamically based on their scans
            List<ScanHistory> userHistory = scanRepository.findByUserIdOrderByTimestampDesc(user.getId());
            int safetyScore = 100;
            for (ScanHistory sh : userHistory) {
                if ("Risky".equalsIgnoreCase(sh.getResult())) {
                    safetyScore -= 15;
                }
            }
            if (safetyScore < 10) safetyScore = 10;
            if (safetyScore > 100) safetyScore = 100;
            user.setSafetyScore(safetyScore);
            userRepository.save(user);
        }

        return Map.of(
                "status", status,
                "score", score,
                "strength", status
        );
    }
}
