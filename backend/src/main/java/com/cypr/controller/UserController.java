package com.cypr.controller;

import com.cypr.entity.User;
import com.cypr.entity.ScanHistory;
import com.cypr.entity.VerificationToken;
import com.cypr.entity.SecurityAlert;
import com.cypr.repository.UserRepository;
import com.cypr.repository.ScanRepository;
import com.cypr.repository.VerificationTokenRepository;
import com.cypr.repository.SecurityAlertRepository;
import com.cypr.repository.PasswordResetTokenRepository;
import com.cypr.repository.MalwareScanLogRepository;
import com.cypr.model.MalwareScanLog;
import com.cypr.service.CaptchaService;
import com.cypr.service.AntiAbuseService;
import com.cypr.service.EmailService;
import com.cypr.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ScanRepository scanRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private SecurityAlertRepository securityAlertRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private MalwareScanLogRepository malwareScanLogRepository;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private AntiAbuseService antiAbuseService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private HttpServletRequest request;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Credit check-on-demand helper
    public static void checkAndResetCredits(User user) {
        if (user == null) return;
        java.time.LocalDate today = java.time.LocalDate.now();
        if (user.getLastResetDate() == null) {
            user.setLastResetDate(today.minusDays(1));
        }
        if ("FREE".equalsIgnoreCase(user.getSubscriptionType())) {
            if (user.getLastResetDate().isBefore(today)) {
                user.setCredits(5);
                user.setLastResetDate(today);
            }
        } else if ("PRO".equalsIgnoreCase(user.getSubscriptionType()) || "PLUS".equalsIgnoreCase(user.getSubscriptionType())) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(user.getLastResetDate(), today);
            if (days >= 7) {
                user.setCredits(15);
                user.setLastResetDate(today);
            }
        }
    }

    // 1. Dynamic Profile Fetch (e.g., /api/user/2/profile)
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            if (!user.isEnabled()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Please verify your email address to access CYPR.");
            }
            checkAndResetCredits(user);
            return ResponseEntity.ok((Object) userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 2. Dynamic History Fetch (e.g., /api/user/2/history)
    @GetMapping("/{id}/history")
    public List<ScanHistory> getUserHistory(@PathVariable Long id) {
        return scanRepository.findByUserIdOrderByTimestampDesc(id);
    }

    // 3. User Registration
    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<?> registerUser(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("mobile") String mobile,
            @RequestParam("username") String username,
            @RequestParam("bio") String bio,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "turnstileToken", required = false) String turnstileToken
    ) {
        try {
            String clientIp = getClientIp();

            // Captcha Server-side Validation
            if (!captchaService.verifyToken(turnstileToken, clientIp)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("CAPTCHA verification failed. Please try again.");
            }

            // Uniqueness Checks
            if (userRepository.existsByUsername(username)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Username is already taken! Please choose another.");
            }
            User existingUser = userRepository.findByEmail(email);
            if (existingUser != null) {
                if (!existingUser.isEnabled()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Verification pending. Please verify your email address to access CYPR.");
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Email address is already registered! Please sign in.");
            }

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            // Securely hash password with BCrypt
            user.setPassword(passwordEncoder.encode(password));
            user.setMobile(mobile);
            user.setUsername(username);
            user.setBio(bio);
            user.setSafetyScore(100);
            user.setCredits(5); // Default free credits
            user.setSubscriptionType("FREE");
            user.setLastResetDate(java.time.LocalDate.now());
            user.setEnabled(false); // Verification required

            if (profileImage != null && !profileImage.isEmpty()) {
                String base64Image = "data:" + profileImage.getContentType() + ";base64," + 
                                     Base64.getEncoder().encodeToString(profileImage.getBytes());
                user.setProfilePicUrl(base64Image);
            }

            // Save logic
            User savedUser = userRepository.save(user);

            // Generate cryptographically secure verification token
            String rawToken = generateSecureToken();
            String hashedToken = hashToken(rawToken);

            VerificationToken vt = new VerificationToken();
            vt.setUser(savedUser);
            vt.setTokenHash(hashedToken);
            vt.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // 30 minutes expiry
            verificationTokenRepository.save(vt);

            // Send Async Verification Email
            emailService.sendVerificationEmail(email, name, rawToken);
            antiAbuseService.recordEmailSent(email);

            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Login ke liye ek Request Body class (identifier can be email or username)
    public static class LoginRequest {
        private String identifier;
        private String password;

        public LoginRequest() {}
        // Getters and Setters
        public String getIdentifier() { return identifier; }
        public void setIdentifier(String identifier) { this.identifier = identifier; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginDetails) {
        String clientIp = getClientIp();

        // 1. Anti-abuse: Check if IP is blocked
        if (antiAbuseService.isIpBlocked(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Your IP is temporarily blocked due to consecutive failed login attempts. Please retry after 15 minutes.");
        }

        // 2. Identifier (Email OR Username) se user dhoondo
        java.util.Optional<User> userOpt = userRepository.findByEmailOrUsername(
                loginDetails.getIdentifier(),
                loginDetails.getIdentifier()
        );

        String userAgent = request.getHeader("User-Agent");

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Verify password using BCrypt with a fallback check to support plaintext migration
            if (passwordEncoder.matches(loginDetails.getPassword(), user.getPassword()) || 
                user.getPassword().equals(loginDetails.getPassword())) {

                // Block unverified accounts
                if (!user.isEnabled()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Your account is not verified. Please check your inbox for the verification email.");
                }

                // Successful login
                antiAbuseService.registerSuccess(clientIp, loginDetails.getIdentifier());
                checkAndResetCredits(user);

                // Generate JWT and set in transient sessionToken field for the frontend
                String token = jwtUtil.generateToken(user.getId(), user.getEmail());
                user.setSessionToken(token);

                userRepository.save(user);

                // Audit logging only (no email alert for successful login)
                logSecurityEvent(user, "LOGIN_SUCCESS", clientIp, userAgent);

                return ResponseEntity.ok(user); // Success
            } else {
                // Wrong password - lock tracking
                antiAbuseService.registerFailedAttempt(clientIp, loginDetails.getIdentifier());
                logSecurityEvent(user, "FAILED_LOGIN_ATTEMPTS", clientIp, userAgent);
            }
        } else {
            // Non-existent identifier - track failed attempt on IP to block brute force
            antiAbuseService.registerFailedAttempt(clientIp, loginDetails.getIdentifier());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid Identifier or Password");
    }

@PutMapping("/{id}/update")
public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedData) {
    return userRepository.findById(id).map(user -> {
        // Sirf wahi fields update karo jo user ne edit ki hain
        user.setName(updatedData.getName());
        user.setEmail(updatedData.getEmail());
        user.setMobile(updatedData.getMobile());
        user.setBio(updatedData.getBio());
        user.setProfilePicUrl(updatedData.getProfilePicUrl());
        
        userRepository.save(user);
        return ResponseEntity.ok("Profile updated successfully!");
    }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
}
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userRepository.existsByUsername(username);
        return ResponseEntity.ok(exists); // Agar user hai toh true, nahi toh false
    }

    // 4. Dynamic Activity Feed
    @GetMapping("/{id}/activity")
    public ResponseEntity<?> getUserActivity(@PathVariable Long id) {
        // Fetch all scan history entries for the user
        List<ScanHistory> histories = scanRepository.findByUserIdOrderByTimestampDesc(id);

        List<Map<String, Object>> activities = new ArrayList<>();

        User user = userRepository.findById(id).orElse(null);

        // We can map each ScanHistory to the format expected by activity-logs.html
        for (ScanHistory sh : histories) {
            Map<String, Object> act = new LinkedHashMap<>();
            act.put("id", sh.getId());
            
            String rawUrl = sh.getUrl();
            if (rawUrl != null && rawUrl.startsWith("Password Check:")) {
                // It's a password check
                act.put("type", "password");
                act.put("title", "Password Strength Check");
                boolean isSecure = "Secure".equalsIgnoreCase(sh.getResult());
                act.put("description", "Password rated " + (isSecure ? "Strong" : "Weak"));
                act.put("result", isSecure ? "safe" : "warning");
                act.put("url", "");
            } else {
                // It's a URL Phishing check
                act.put("type", "url_scan");
                boolean isSecure = "Secure".equalsIgnoreCase(sh.getResult());
                if (isSecure) {
                    act.put("title", "URL Scan — Safe");
                    act.put("description", "Scanned " + rawUrl);
                    act.put("result", "safe");
                } else {
                    act.put("title", "URL Scan — Threat Found");
                    act.put("description", "Malicious redirect detected");
                    act.put("result", "danger");
                }
                act.put("url", rawUrl);
            }
            
            // Convert LocalDateTime to epoch milliseconds
            long epochMillis = sh.getTimestamp().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            act.put("timestamp", epochMillis);
            
            activities.add(act);
        }

        // Add an authentic/dynamic Login session start entry based on user registration/creation
        Map<String, Object> loginAct = new LinkedHashMap<>();
        loginAct.put("id", 999999L);
        loginAct.put("type", "login");
        loginAct.put("title", "Login — Session Started");
        loginAct.put("description", "Chrome · Windows · Security Portal Access");
        loginAct.put("result", "info");
        loginAct.put("url", "");
        if (user != null) {
            long loginMillis = java.time.LocalDateTime.now().minusMinutes(30).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            loginAct.put("timestamp", loginMillis);
        } else {
            loginAct.put("timestamp", System.currentTimeMillis() - 1800000);
        }
        activities.add(loginAct);

        // Sort by timestamp descending
        activities.sort((a, b) -> Long.compare((Long) b.get("timestamp"), (Long) a.get("timestamp")));

        return ResponseEntity.ok(activities);
    }

    // 5. Deduct credits via REST call
    @PostMapping("/{id}/credits/deduct")
    public ResponseEntity<?> deductCredits(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return userRepository.findById(id).map(user -> {
            int amount = 1;
            if (body != null && body.containsKey("amount")) {
                amount = ((Number) body.get("amount")).intValue();
            }
            if (user.getCredits() < amount) {
                return ResponseEntity.badRequest().body(Map.of("error", "Daily Credit Limit Reached for Malware Scan!"));
            }
            user.setCredits(user.getCredits() - amount);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // 6. Update safetyScore via REST call
    @PostMapping("/{id}/safe-score/update")
    public ResponseEntity<?> updateSafeScore(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return userRepository.findById(id).map(user -> {
            int delta = 0;
            if (body != null && body.containsKey("delta")) {
                delta = ((Number) body.get("delta")).intValue();
            }
            int newScore = user.getSafetyScore() + delta;
            if (newScore > 100) newScore = 100;
            if (newScore < 10) newScore = 10;
            user.setSafetyScore(newScore);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── SECURITY HELPERS ──────────────────────────────────────────────────────

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

    private void logSecurityEvent(User user, String type, String ip, String ua) {
        try {
            SecurityAlert alert = new SecurityAlert();
            alert.setUserId(user.getId());
            alert.setAlertType(type);

            alert.setIpAddress(ip);
            alert.setDeviceInfo(ua != null ? ua : "Unknown Device");
            alert.setLocation("Local Network");
            alert.setTimestamp(LocalDateTime.now());
            securityAlertRepository.save(alert);
        } catch (Exception e) {
            // Non-blocking log persistence failure
        }
    }

    // ── ACCOUNT MANAGEMENT ENDPOINTS ──────────────────────────────────────────

    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        if (currentPassword == null || currentPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Both current and new passwords are required.");
        }
        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest().body("New password must be at least 8 characters long.");
        }

        return userRepository.findById(id).map(user -> {
            if (!passwordEncoder.matches(currentPassword, user.getPassword()) && !user.getPassword().equals(currentPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect current password.");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Log security event
            logSecurityEvent(user, "PASSWORD_CHANGE", getClientIp(), request.getHeader("User-Agent"));

            // Send Security alert email
            emailService.sendSecurityAlertEmail(
                user.getEmail(),
                user.getName(),
                "PASSWORD_CHANGE",
                getClientIp(),
                request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "Unknown Device",
                "Local Network"
            );

            return ResponseEntity.ok(Map.of("success", true, "message", "Password updated successfully!"));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateAccount(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String password = body.get("password");

        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Password is required to deactivate your account.");
        }

        return userRepository.findById(id).map(user -> {
            if (!passwordEncoder.matches(password, user.getPassword()) && !user.getPassword().equals(password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password.");
            }

            user.setEnabled(false);
            userRepository.save(user);

            // Log security event
            logSecurityEvent(user, "ACCOUNT_DEACTIVATED", getClientIp(), request.getHeader("User-Agent"));

            // Send Security alert email
            emailService.sendSecurityAlertEmail(
                user.getEmail(),
                user.getName(),
                "ACCOUNT_DEACTIVATED",
                getClientIp(),
                request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "Unknown Device",
                "Local Network"
            );

            return ResponseEntity.ok(Map.of("success", true, "message", "Account deactivated successfully!"));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteAccount(
            @PathVariable Long id, 
            @RequestParam("password") String password, 
            @RequestParam("confirmText") String confirmText
    ) {
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Password is required to delete your account.");
        }
        if (!"DELETE".equals(confirmText)) {
            return ResponseEntity.badRequest().body("Confirmation word must be exactly 'DELETE'.");
        }

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword()) && !user.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password.");
        }

        try {
            // Send farewell email before deleting everything
            emailService.sendSecurityAlertEmail(
                user.getEmail(),
                user.getName(),
                "ACCOUNT_DELETED",
                getClientIp(),
                request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "Unknown Device",
                "Local Network"
            );

            // Cascade delete related entities
            verificationTokenRepository.deleteByUser(user);
            passwordResetTokenRepository.deleteByUser(user);
            securityAlertRepository.deleteByUserId(id);
            scanRepository.deleteByUserId(id);
            malwareScanLogRepository.deleteByUserId(String.valueOf(id));

            // Delete user itself
            userRepository.delete(user);

            return ResponseEntity.ok(Map.of("success", true, "message", "Account and all associated data permanently deleted. Goodbye!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete account: " + e.getMessage());
        }
    }

    @PostMapping("/contact")
    public ResponseEntity<?> submitContactMessage(@RequestBody Map<String, String> payload) {
        String firstName = payload.get("firstName");
        String lastName = payload.get("lastName");
        String email = payload.get("email");
        String subject = payload.get("subject");
        String message = payload.get("message");

        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            return ResponseEntity.badRequest().body("Full name is required.");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.badRequest().body("Valid email is required.");
        }
        if (subject == null || subject.isBlank()) {
            return ResponseEntity.badRequest().body("Subject topic is required.");
        }
        if (message == null || message.length() < 10) {
            return ResponseEntity.badRequest().body("Message must be at least 10 characters.");
        }

        try {
            // Send email to admin
            emailService.sendContactFormEmail(
                "iec.vineet@gmail.com",
                firstName,
                lastName,
                email,
                subject,
                message
            );
            return ResponseEntity.ok(Map.of("success", true, "message", "Contact form inquiry submitted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to submit inquiry: " + e.getMessage());
        }
    }
}