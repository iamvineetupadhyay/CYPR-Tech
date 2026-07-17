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
import com.cypr.security.SecurityUtils;
import com.cypr.security.HtmlSanitizer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cypr.common.BaseResponse;
import com.cypr.modules.security.repository.ActivityLogRepository;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ScanRepository scanRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

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

    // 1. Dynamic Profile Fetch (e.g., /api/user/me/profile)
    @GetMapping("/me/profile")
    public ResponseEntity<?> getProfile() {
        Long id = SecurityUtils.getCurrentUserId(request);
        if (id == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return userRepository.findById(id).map(user -> {
            if (!user.isEnabled()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Please verify your email address to access CYPR.");
            }
            checkAndResetCredits(user);
            return ResponseEntity.ok((Object) userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 2. Dynamic History Fetch (e.g., /api/user/me/history)
    @GetMapping("/me/history")
    public List<ScanHistory> getUserHistory() {
        Long id = SecurityUtils.getCurrentUserId(request);
        if (id == null) return Collections.emptyList();
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
            user.setBio(HtmlSanitizer.sanitize(bio));
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
            log.info("[Registration Flow] Secure verification token generated and hashed token securely saved for user email: {}", email);

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

            if (user.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("This account uses Google/GitHub sign-in. Please log in with that provider.");
            }

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
                String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
                user.setSessionToken(token);
                user.setRefreshToken(refreshToken);

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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }
        
        Long userId = jwtUtil.extractUserId(refreshToken);
        String email = jwtUtil.extractEmail(refreshToken);
        
        if (userId != null && email != null) {
            String newAccessToken = jwtUtil.generateToken(userId, email);
            String newRefreshToken = jwtUtil.generateRefreshToken(userId, email);
            
            return ResponseEntity.ok(Map.of(
                "sessionToken", newAccessToken,
                "refreshToken", newRefreshToken
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token payload");
    }

@PutMapping("/me/update")
public ResponseEntity<?> updateUser(@RequestBody User updatedData) {
    Long id = SecurityUtils.getCurrentUserId(request);
    if (id == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    return userRepository.findById(id).map(user -> {
        // Sirf wahi fields update karo jo user ne edit ki hain
        user.setName(updatedData.getName());
        user.setEmail(updatedData.getEmail());
        user.setMobile(updatedData.getMobile());
        user.setBio(HtmlSanitizer.sanitize(updatedData.getBio()));
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

    // 4. Dynamic Activity Feed (IDOR-Protected)
    @GetMapping({"/me/activity", "/{id}/activity"})
    public ResponseEntity<?> getUserActivity(
            @PathVariable(required = false) Long id,
            HttpServletRequest request) {

        Long authUserId = SecurityUtils.getCurrentUserId(request);
        if (authUserId == null) authUserId = 1L; // Fallback for dev session

        // C5: IDOR Protection — user can only view their own activity unless admin
        if (id != null && !id.equals(authUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(BaseResponse.error("Access denied: You are not authorized to view activity logs for another account."));
        }
        Long targetUserId = (id != null) ? id : authUserId;

        // Fetch scan history, malware logs, and database activity logs for the target user
        List<ScanHistory> histories = scanRepository.findByUserIdOrderByTimestampDesc(targetUserId);
        List<MalwareScanLog> malwareLogs = malwareScanLogRepository.findByUserIdOrderByScannedAtDesc(targetUserId.toString());
        List<com.cypr.modules.security.entity.ActivityLog> dbLogs = activityLogRepository.findByUserId(targetUserId);

        List<Map<String, Object>> activities = new ArrayList<>();

        // Map real MalwareScanLog DB entries
        for (MalwareScanLog mlog : malwareLogs) {
            Map<String, Object> act = new LinkedHashMap<>();
            act.put("id", "mw_" + mlog.getId());
            act.put("type", "malware_scan");
            boolean isClean = "CLEAN".equalsIgnoreCase(mlog.getThreatLevel());
            act.put("title", isClean ? "Malware Deep Analysis — Clean" : "Malware Deep Analysis — Threat Detected");
            act.put("description", "File: " + mlog.getFileName() + " · Threat Level: " + mlog.getThreatLevel() + (mlog.getDetectedThreats() != null && !mlog.getDetectedThreats().isEmpty() ? " (" + mlog.getDetectedThreats() + ")" : ""));
            act.put("result", isClean ? "safe" : "danger");
            act.put("scoreDelta", mlog.getSafeScoreDelta());
            act.put("url", mlog.getFileName());
            if (mlog.getScannedAt() != null) {
                act.put("timestamp", mlog.getScannedAt().toEpochMilli());
            } else {
                act.put("timestamp", System.currentTimeMillis());
            }
            activities.add(act);
        }

        // Map ScanHistory entries
        for (ScanHistory sh : histories) {
            Map<String, Object> act = new LinkedHashMap<>();
            act.put("id", sh.getId());

            String rawUrl = sh.getUrl();
            if (rawUrl != null && rawUrl.startsWith("Password Check:")) {
                act.put("type", "password");
                act.put("title", "Password Strength Check");
                boolean isSecure = "Secure".equalsIgnoreCase(sh.getResult());
                act.put("description", "Password rated " + (isSecure ? "Strong (88/100)" : "Weak (24/100)"));
                act.put("result", isSecure ? "safe" : "warning");
                act.put("scoreDelta", isSecure ? 5 : -10);
                act.put("url", "");
            } else if (rawUrl != null && rawUrl.startsWith("Malware:")) {
                act.put("type", "malware_scan");
                act.put("title", "Malware Deep Analysis");
                boolean isSecure = "Secure".equalsIgnoreCase(sh.getResult());
                act.put("description", "File: " + rawUrl.substring(8) + " · Verdict: " + (isSecure ? "Clean (0 engines flagged)" : "Malicious (8/64 engines)"));
                act.put("result", isSecure ? "safe" : "danger");
                act.put("scoreDelta", isSecure ? 10 : -25);
                act.put("url", rawUrl.substring(8));
            } else if (rawUrl != null && rawUrl.startsWith("Code Audit:")) {
                act.put("type", "code_audit");
                act.put("title", "Code Security Audit");
                boolean isSecure = "Secure".equalsIgnoreCase(sh.getResult());
                act.put("description", "Snippet: " + rawUrl.substring(11) + " · SAST Score: " + (isSecure ? "Passed" : "Vulnerability Detected"));
                act.put("result", isSecure ? "safe" : "warning");
                act.put("scoreDelta", isSecure ? 8 : -12);
                act.put("url", "");
            } else if (rawUrl != null && rawUrl.startsWith("Breach:")) {
                act.put("type", "breach_check");
                act.put("title", "Breach Radar Leak Discovery");
                boolean isSecure = "Secure".equalsIgnoreCase(sh.getResult());
                act.put("description", "Target: " + rawUrl.substring(7) + " · Leaks Found: " + (isSecure ? "None (Safe)" : "Exposed in 2 public dumps"));
                act.put("result", isSecure ? "safe" : "danger");
                act.put("scoreDelta", isSecure ? 0 : -20);
                act.put("url", rawUrl.substring(7));
            } else {
                act.put("type", "url_scan");
                boolean isSecure = "Secure".equalsIgnoreCase(sh.getResult());
                act.put("title", isSecure ? "URL Scan — Safe" : "URL Scan — Threat Found");
                act.put("description", isSecure ? "Scanned " + rawUrl : "Malicious redirect or phishing domain detected");
                act.put("result", isSecure ? "safe" : "danger");
                act.put("scoreDelta", isSecure ? 3 : -15);
                act.put("url", rawUrl);
            }

            long epochMillis = sh.getTimestamp().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            act.put("timestamp", epochMillis);
            activities.add(act);
        }

        // Map DB ActivityLog entries
        for (com.cypr.modules.security.entity.ActivityLog dbLog : dbLogs) {
            Map<String, Object> act = new LinkedHashMap<>();
            act.put("id", dbLog.getId().toString());
            act.put("type", dbLog.getEntityType() != null ? dbLog.getEntityType().toLowerCase() : "login");
            act.put("title", dbLog.getAction());
            act.put("description", dbLog.getDetails() != null ? dbLog.getDetails() : "Security event logged");
            act.put("result", dbLog.getResult() != null ? dbLog.getResult() : "info");
            act.put("scoreDelta", dbLog.getScoreDelta() != null ? dbLog.getScoreDelta() : 0);
            act.put("url", dbLog.getUrl() != null ? dbLog.getUrl() : "");

            if (dbLog.getCreatedAt() != null) {
                long epochMillis = dbLog.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                act.put("timestamp", epochMillis);
            } else {
                act.put("timestamp", System.currentTimeMillis());
            }
            activities.add(act);
        }

        // Add dynamic Session Start event if empty
        if (activities.isEmpty()) {
            Map<String, Object> loginAct = new LinkedHashMap<>();
            loginAct.put("id", "sys_login_1");
            loginAct.put("type", "login");
            loginAct.put("title", "Login — Session Started");
            loginAct.put("description", "Chrome · Windows · Security Portal Access");
            loginAct.put("result", "info");
            loginAct.put("scoreDelta", 0);
            loginAct.put("url", "");
            loginAct.put("timestamp", System.currentTimeMillis() - 600000);
            activities.add(loginAct);
        }

        // Sort by timestamp descending
        activities.sort((a, b) -> Long.compare((Long) b.get("timestamp"), (Long) a.get("timestamp")));

        return ResponseEntity.ok(activities);
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

    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body) {
        Long id = SecurityUtils.getCurrentUserId(request);
        if (id == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        if (currentPassword == null || currentPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Both current and new passwords are required.");
        }
        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest().body("New password must be at least 8 characters long.");
        }

        return userRepository.findById(id).map(user -> {
            if (user.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("This account uses Google/GitHub sign-in. Passwords cannot be changed.");
            }

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

    @PostMapping("/me/deactivate")
    public ResponseEntity<?> deactivateAccount(@RequestBody Map<String, String> body) {
        Long id = SecurityUtils.getCurrentUserId(request);
        if (id == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String password = body.get("password");

        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Password is required to deactivate your account.");
        }

        return userRepository.findById(id).map(user -> {
            if (user.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("This account uses Google/GitHub sign-in. Please log in with that provider.");
            }

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

    @DeleteMapping("/me/delete")
    public ResponseEntity<?> deleteAccount(
            @RequestParam("password") String password, 
            @RequestParam("confirmText") String confirmText
    ) {
        Long id = SecurityUtils.getCurrentUserId(request);
        if (id == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

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
        
        if (user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This account uses Google/GitHub sign-in. Please log in with that provider to manage your account.");
        }

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