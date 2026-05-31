package com.cypr.controller;

import com.cypr.config.JwtUtil;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

    // ── GOOGLE CONFIG ──────────────────────────────────────────────────────────
    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleClientSecret;

    @Value("${google.redirect.uri}")
    private String googleRedirectUri;

    // ── GITHUB CONFIG ──────────────────────────────────────────────────────────
    @Value("${github.client.id:NOT_SET}")
    private String githubClientId;

    @Value("${github.client.secret:NOT_SET}")
    private String githubClientSecret;

    @Value("${github.redirect.uri:NOT_SET}")
    private String githubRedirectUri;

    // ── FRONTEND CALLBACK URL ──────────────────────────────────────────────────
    @Value("${cypr.frontend.base-url}")
    private String frontendBaseUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ═══════════════════════════════════════════════════════════════════════════
    //  GOOGLE
    // ═══════════════════════════════════════════════════════════════════════════

    /** Step 1 — Redirect browser to Google consent screen */
    @GetMapping("/google/authorize")
    public void googleAuthorize(HttpServletResponse response) throws IOException {
        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + encode(googleClientId)
                + "&redirect_uri=" + encode(googleRedirectUri)
                + "&response_type=code"
                + "&scope=" + encode("openid email profile")
                + "&access_type=offline"
                + "&prompt=select_account";
        response.sendRedirect(url);
    }

    /** Step 2 — Google sends code here; exchange it, upsert user, redirect to frontend */
    @GetMapping("/google/callback")
    public void googleCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        try {
            // Exchange authorization code for tokens
            String tokenJson = exchangeGoogleCode(code);
            JsonNode tokenNode = objectMapper.readTree(tokenJson);
            String accessToken = tokenNode.get("access_token").asText();

            // Fetch user profile from Google
            JsonNode profile = fetchGoogleProfile(accessToken);
            String oauthId  = profile.get("id").asText();
            String email    = profile.has("email")   ? profile.get("email").asText()   : "";
            String name     = profile.has("name")    ? profile.get("name").asText()    : "User";
            String avatar   = profile.has("picture") ? profile.get("picture").asText() : "";

            // Upsert: find by oauthId or email, else create new
            User user = findOrCreateOAuthUser(oauthId, email, name, avatar, "GOOGLE");

            // Generate JWT
            String jwt = jwtUtil.generateToken(user.getId(), user.getEmail());
            user.setSessionToken(jwt);
            userRepository.save(user);

            // Build frontend redirect URL with all session params
            String redirectUrl = buildFrontendCallbackUrl(user, jwt);
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            response.sendRedirect(frontendBaseUrl + "/login.html?oauth_error=" + encode(e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  GITHUB
    // ═══════════════════════════════════════════════════════════════════════════

    /** Step 1 — Redirect browser to GitHub consent screen */
    @GetMapping("/github/authorize")
    public void githubAuthorize(HttpServletResponse response) throws IOException {
        if ("NOT_SET".equals(githubClientId)) {
            response.sendRedirect(frontendBaseUrl + "/login.html?oauth_error=GitHub+OAuth+not+configured+yet");
            return;
        }
        String url = "https://github.com/login/oauth/authorize"
                + "?client_id=" + encode(githubClientId)
                + "&redirect_uri=" + encode(githubRedirectUri)
                + "&scope=" + encode("user:email")
                + "&allow_signup=true";
        response.sendRedirect(url);
    }

    /** Step 2 — GitHub sends code here; exchange it, upsert user, redirect to frontend */
    @GetMapping("/github/callback")
    public void githubCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        try {
            // Exchange code for access token
            String accessToken = exchangeGithubCode(code);

            // Fetch profile
            JsonNode profile = fetchGithubProfile(accessToken);
            String oauthId = String.valueOf(profile.get("id").asLong());
            String name    = profile.has("name") && !profile.get("name").isNull()
                             ? profile.get("name").asText() : profile.get("login").asText();
            String avatar  = profile.has("avatar_url") ? profile.get("avatar_url").asText() : "";

            // GitHub may not expose email directly — fetch from /user/emails
            String email = fetchGithubEmail(accessToken);

            User user = findOrCreateOAuthUser(oauthId, email, name, avatar, "GITHUB");

            String jwt = jwtUtil.generateToken(user.getId(), user.getEmail());
            user.setSessionToken(jwt);
            userRepository.save(user);

            String redirectUrl = buildFrontendCallbackUrl(user, jwt);
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            response.sendRedirect(frontendBaseUrl + "/login.html?oauth_error=" + encode(e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════════════════════

    private String exchangeGoogleCode(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<String> resp = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                new HttpEntity<>(params, headers),
                String.class);
        return resp.getBody();
    }

    private JsonNode fetchGoogleProfile(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        ResponseEntity<String> resp = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);
        return objectMapper.readTree(resp.getBody());
    }

    private String exchangeGithubCode(String code) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", githubClientId);
        params.add("client_secret", githubClientSecret);
        params.add("code", code);
        params.add("redirect_uri", githubRedirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept", "application/json");

        ResponseEntity<String> resp = restTemplate.postForEntity(
                "https://github.com/login/oauth/access_token",
                new HttpEntity<>(params, headers),
                String.class);

        JsonNode node = objectMapper.readTree(resp.getBody());
        return node.get("access_token").asText();
    }

    private JsonNode fetchGithubProfile(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github+json");
        ResponseEntity<String> resp = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);
        return objectMapper.readTree(resp.getBody());
    }

    private String fetchGithubEmail(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github+json");
        ResponseEntity<String> resp = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);
        JsonNode emails = objectMapper.readTree(resp.getBody());
        // Prefer primary + verified email
        for (JsonNode e : emails) {
            if (e.has("primary") && e.get("primary").asBoolean()
                    && e.has("verified") && e.get("verified").asBoolean()) {
                return e.get("email").asText();
            }
        }
        // Fallback: first available email
        if (emails.isArray() && emails.size() > 0) {
            return emails.get(0).get("email").asText();
        }
        return "";
    }

    /**
     * Find existing user by oauthId+provider or by email.
     * If not found, create a new auto-verified account.
     */
    private User findOrCreateOAuthUser(String oauthId, String email, String name, String avatar, String provider) {
        // 1. Try by oauthId + provider
        Optional<User> byOauth = userRepository.findByOauthProviderAndOauthId(provider, oauthId);
        if (byOauth.isPresent()) return byOauth.get();

        // 2. Try by email (user might have signed up with email before)
        if (email != null && !email.isEmpty()) {
            User existing = userRepository.findByEmail(email);
            if (existing != null) {
                // Link OAuth to existing account
                existing.setOauthProvider(provider);
                existing.setOauthId(oauthId);
                if (avatar != null && !avatar.isEmpty() && (existing.getProfilePicUrl() == null || existing.getProfilePicUrl().isEmpty())) {
                    existing.setProfilePicUrl(avatar);
                }
                existing.setEnabled(true);
                return userRepository.save(existing);
            }
        }

        // 3. Create new user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(null); // OAuth users have no password
        user.setUsername(generateUsername(name, oauthId));
        user.setBio("Joined via " + provider);
        user.setProfilePicUrl(avatar);
        user.setOauthProvider(provider);
        user.setOauthId(oauthId);
        user.setSafetyScore(100);
        user.setCredits(5); // Default free credits (same as email signup)
        user.setSubscriptionType("FREE");
        user.setLastResetDate(LocalDate.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true); // Auto-verified via OAuth
        return userRepository.save(user);
    }

    private String generateUsername(String name, String oauthId) {
        // e.g. "vineet_a3f2"
        String base = name.toLowerCase().replaceAll("[^a-z0-9]", "").substring(0, Math.min(name.length(), 10));
        String suffix = oauthId.substring(oauthId.length() - 4);
        String candidate = base + "_" + suffix;
        // Ensure uniqueness
        int i = 0;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + "_" + suffix + i;
            i++;
        }
        return candidate;
    }

    private String buildFrontendCallbackUrl(User user, String jwt) {
        String name = user.getName() != null ? user.getName() : "User";
        String init = name.chars()
                .filter(c -> c == ' ' || name.indexOf(c) == 0)
                .limit(2)
                .collect(StringBuilder::new, (sb, c) -> sb.append((char) c), StringBuilder::append)
                .toString().trim().toUpperCase();
        if (init.isEmpty()) init = name.substring(0, Math.min(2, name.length())).toUpperCase();

        return frontendBaseUrl + "/oauth-callback.html"
                + "?token=" + encode(jwt)
                + "&userId=" + user.getId()
                + "&name=" + encode(name)
                + "&email=" + encode(user.getEmail() != null ? user.getEmail() : "")
                + "&avatar=" + encode(user.getProfilePicUrl() != null ? user.getProfilePicUrl() : "")
                + "&initials=" + encode(init)
                + "&credits=" + user.getCredits()
                + "&subscription=" + encode(user.getSubscriptionType())
                + "&score=" + user.getSafetyScore();
    }

    private String encode(String val) {
        return URLEncoder.encode(val != null ? val : "", StandardCharsets.UTF_8);
    }
}
