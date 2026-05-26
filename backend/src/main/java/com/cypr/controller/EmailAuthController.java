package com.cypr.controller;

import org.springframework.transaction.annotation.Transactional;
import com.cypr.entity.User;
import com.cypr.entity.VerificationToken;
import com.cypr.entity.PasswordResetToken;
import com.cypr.entity.SecurityAlert;
import com.cypr.repository.UserRepository;
import com.cypr.repository.VerificationTokenRepository;
import com.cypr.repository.PasswordResetTokenRepository;
import com.cypr.repository.SecurityAlertRepository;
import com.cypr.service.EmailService;
import com.cypr.service.AntiAbuseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class EmailAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private SecurityAlertRepository securityAlertRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AntiAbuseService antiAbuseService;

    @Autowired
    private HttpServletRequest request;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ── EMAIL VERIFICATION ENDPOINT ───────────────────────────────────────────

    @GetMapping("/verify")
    @Transactional
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        String tokenHash = hashToken(token);
        Optional<VerificationToken> tokenOpt = verificationTokenRepository.findByTokenHash(tokenHash);

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired verification token.");
        }

        VerificationToken vt = tokenOpt.get();
        if (vt.isUsed()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Verification token has already been used.");
        }

        if (LocalDateTime.now().isAfter(vt.getExpiryDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Verification token has expired. Please request a new one.");
        }

        User user = vt.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        vt.setUsed(true);
        verificationTokenRepository.save(vt);

        // Send Welcome Message email
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());

        // Record a security event log
        logSecurityEvent(user, "ACCOUNT_ACTIVATION_SUCCESS");

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Namaste! Your account has been verified successfully. You can now sign in."
        ));
    }

    // ── RESEND VERIFICATION ENDPOINT ──────────────────────────────────────────

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam("email") String email) {
        String trimmedEmail = email.trim();
        User user = userRepository.findByEmail(trimmedEmail);

        // Generic response to avoid email enumeration attacks
        String genericResponse = "If this email is registered, a new verification link has been sent.";

        if (user == null) {
            return ResponseEntity.ok(Map.of("message", genericResponse));
        }

        if (user.isEnabled()) {
            return ResponseEntity.ok(Map.of("message", "This account is already verified. Please sign in."));
        }

        // Anti-abuse: Cooldown (60 seconds)
        if (!antiAbuseService.checkEmailCooldown(trimmedEmail)) {
            long remaining = antiAbuseService.getRemainingCooldownSeconds(trimmedEmail);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Please wait " + remaining + " seconds before requesting another email.");
        }

        // Anti-abuse: Hourly rate limit (5 emails)
        if (antiAbuseService.isEmailLimitExceeded(trimmedEmail)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Email limit exceeded. Please try again after some time.");
        }

        // Generate new token
        String rawToken = generateSecureToken();
        String hashedToken = hashToken(rawToken);

        VerificationToken vt = new VerificationToken();
        vt.setUser(user);
        vt.setTokenHash(hashedToken);
        vt.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // 30 minutes expiry
        verificationTokenRepository.save(vt);

        // Async sending
        emailService.sendVerificationEmail(trimmedEmail, user.getName(), rawToken);
        antiAbuseService.recordEmailSent(trimmedEmail);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "A fresh verification link has been sent to your email address."
        ));
    }

    // ── FORGOT PASSWORD (REQUEST LINK) ENDPOINT ──────────────────────────────

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.trim().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }
        
        String trimmedEmail = email.trim();
        User user = userRepository.findByEmail(trimmedEmail);
        String genericSuccessResponse = "If the account exists, a recovery email has been sent.";

        // Anti-abuse: Hourly rate limit
        if (antiAbuseService.isEmailLimitExceeded(trimmedEmail)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many requests. Please try again later.");
        }

        if (user == null) {
            // Keep generic to prevent account enumeration
            return ResponseEntity.ok(Map.of("message", genericSuccessResponse));
        }

        // Generate recovery token
        String rawToken = generateSecureToken();
        String hashedToken = hashToken(rawToken);

        PasswordResetToken prt = new PasswordResetToken();
        prt.setUser(user);
        prt.setTokenHash(hashedToken);
        prt.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // 15 minutes recovery window
        passwordResetTokenRepository.save(prt);

        // Async email transmission
        emailService.sendPasswordResetEmail(trimmedEmail, user.getName(), rawToken);
        antiAbuseService.recordEmailSent(trimmedEmail);

        return ResponseEntity.ok(Map.of("message", genericSuccessResponse));
    }

    // ── RESET PASSWORD (CONFIRM PASSWORD CHANGE) ENDPOINT ────────────────────

    @PostMapping("/reset-password")
    @Transactional 
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("Recovery token is missing.");
        }
        if (newPassword == null || newPassword.length() < 8) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters long.");
        }

        String tokenHash = hashToken(token);
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByTokenHash(tokenHash);

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired recovery token.");
        }

        PasswordResetToken prt = tokenOpt.get();
        if (prt.isUsed()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This recovery token has already been used.");
        }

        if (LocalDateTime.now().isAfter(prt.getExpiryDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This recovery token has expired.");
        }

        User user = prt.getUser();
        
        // Hash password with BCrypt
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        prt.setUsed(true);
        passwordResetTokenRepository.save(prt);

        // Security Advisor Log and Alerts
        String clientIp = getClientIp();
        String userAgent = request.getHeader("User-Agent");
        logSecurityEvent(user, "PASSWORD_CHANGE");

        // Send Async Security Advisory Email
        emailService.sendSecurityAlertEmail(
                user.getEmail(),
                user.getName(),
                "PASSWORD_CHANGE",
                clientIp,
                userAgent != null ? userAgent : "Unknown Device",
                "Local Network"
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Password has been successfully updated. You can now log in."
        ));
    }

    // ── UTILITIES & HELPER METHODS ────────────────────────────────────────────

    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return bytesToHex(randomBytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hashing failed", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    private void logSecurityEvent(User user, String type) {
        try {
            SecurityAlert alert = new SecurityAlert();
            alert.setUserId(user.getId());
            alert.setAlertType(type);

            alert.setIpAddress(getClientIp());
            String ua = request.getHeader("User-Agent");
            alert.setDeviceInfo(ua != null ? ua : "Unknown Device");
            alert.setLocation("Local Network");
            alert.setTimestamp(LocalDateTime.now());
            securityAlertRepository.save(alert);
        } catch (Exception e) {
            // Non-blocking log failure
        }
    }
}
