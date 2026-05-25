package com.cypr.service;

import com.cypr.entity.EmailLog;
import com.cypr.repository.EmailLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final EmailLogRepository emailLogRepository;
    private final RestTemplate rest = new RestTemplate();

    @Value("${brevo.api.key:}")
    private String apiKey;

    @Value("${brevo.sender.email:security@cypr.com}")
    private String senderEmail;

    @Value("${brevo.sender.name:CYPR Security}")
    private String senderName;

    @Value("${cypr.frontend.base-url:http://localhost:5500}")
    private String frontendBaseUrl;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    public EmailService(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    // ── PUBLIC INTERFACES (ASYNC & RELIABLE) ──────────────────────────────────

    @Async("emailExecutor")
    public void sendVerificationEmail(String email, String name, String rawToken) {
        String verifyUrl = frontendBaseUrl + "/login.html?verify_token=" + rawToken;
        String subject = "Verify Your CYPR Account";

        String htmlContent = "<html>" +
                "<body style=\"margin:0;padding:0;background-color:#f8fafc;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;color:#334155;\">" +
                "  <div style=\"max-width:540px;margin:40px auto;background-color:#ffffff;border:1px solid #e2e8f0;border-radius:8px;padding:40px;box-shadow:0 4px 6px -1px rgba(0,0,0,0.05);\">" +
                "    <div style=\"text-align:center;margin-bottom:32px;\">" +
                "      <img src=\"" + frontendBaseUrl + "/assets/default-news.jpg\" alt=\"CYPR\" style=\"width:130px;height:auto;display:block;margin:0 auto;\">" +
                "    </div>" +
                "    <h2 style=\"font-size:20px;font-weight:700;color:#0f172a;margin:0 0 16px 0;text-align:center;\">Account Verification Required</h2>" +
                "    <p style=\"font-size:14px;color:#334155;line-height:1.6;margin:0 0 24px 0;\">Hello " + name + ",</p>" +
                "    <p style=\"font-size:14px;color:#334155;line-height:1.6;margin:0 0 24px 0;\">Thank you for registering with CYPR. Please verify your email address to complete your account setup and activate your security dashboard.</p>" +
                "    <div style=\"text-align:center;margin:32px 0;\">" +
                "      <a href=\"" + verifyUrl + "\" style=\"display:inline-block;background-color:#0f172a;color:#ffffff;font-weight:600;font-size:14px;text-decoration:none;padding:13px 28px;border-radius:6px;\">Verify Account</a>" +
                "    </div>" +
                "    <p style=\"font-size:12px;color:#64748b;line-height:1.5;margin:24px 0 0 0;\">If the button above does not work, please copy and paste the following URL into your browser:<br>" +
                "    <a href=\"" + verifyUrl + "\" style=\"color:#2563eb;text-decoration:underline;\">" + verifyUrl + "</a></p>" +
                "    <div style=\"margin:32px 0;border-top:1px solid #e2e8f0;\"></div>" +
                "    <p style=\"font-size:12px;color:#64748b;line-height:1.5;margin:0 0 16px 0;\">This verification link will expire in 30 minutes. If you did not create a CYPR account, please disregard this email.</p>" +
                "    <p style=\"font-size:12px;color:#94a3b8;margin:0;\">&copy; 2026 CYPR Security. All rights reserved.</p>" +
                "  </div>" +
                "</body>" +
                "</html>";

        executeWithRetryAndLog(email, subject, htmlContent);
    }

    @Async("emailExecutor")
    public void sendPasswordResetEmail(String email, String name, String rawToken) {
        String resetUrl = frontendBaseUrl + "/forgotpassword.html?reset_token=" + rawToken;
        String subject = "CYPR Password Reset Request";

        String htmlContent = "<html>" +
                "<body style=\"margin:0;padding:0;background-color:#f8fafc;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;color:#334155;\">" +
                "  <div style=\"max-width:540px;margin:40px auto;background-color:#ffffff;border:1px solid #e2e8f0;border-radius:8px;padding:40px;box-shadow:0 4px 6px -1px rgba(0,0,0,0.05);\">" +
                "    <div style=\"text-align:center;margin-bottom:32px;\">" +
                "      <img src=\"" + frontendBaseUrl + "/assets/default-news.jpg\" alt=\"CYPR\" style=\"width:130px;height:auto;display:block;margin:0 auto;\">" +
                "    </div>" +
                "    <h2 style=\"font-size:20px;font-weight:700;color:#0f172a;margin:0 0 16px 0;text-align:center;\">Password Reset Request</h2>" +
                "    <p style=\"font-size:14px;color:#334155;line-height:1.6;margin:0 0 24px 0;\">Hello " + name + ",</p>" +
                "    <p style=\"font-size:14px;color:#334155;line-height:1.6;margin:0 0 24px 0;\">We received a request to recover and reset the password associated with your CYPR account. Click the button below to set a new password.</p>" +
                "    <div style=\"text-align:center;margin:32px 0;\">" +
                "      <a href=\"" + resetUrl + "\" style=\"display:inline-block;background-color:#0f172a;color:#ffffff;font-weight:600;font-size:14px;text-decoration:none;padding:13px 28px;border-radius:6px;\">Reset Password</a>" +
                "    </div>" +
                "    <p style=\"font-size:12px;color:#64748b;line-height:1.5;margin:24px 0 0 0;\">If the button above does not work, please copy and paste the following URL into your browser:<br>" +
                "    <a href=\"" + resetUrl + "\" style=\"color:#2563eb;text-decoration:underline;\">" + resetUrl + "</a></p>" +
                "    <div style=\"margin:32px 0;border-top:1px solid #e2e8f0;\"></div>" +
                "    <p style=\"font-size:12px;color:#b91c1c;line-height:1.5;margin:0 0 16px 0;\">This recovery link is active for 15 minutes. For security, if you did not request this recovery, please change your credentials immediately or contact support.</p>" +
                "    <p style=\"font-size:12px;color:#94a3b8;margin:0;\">&copy; 2026 CYPR Security. All rights reserved.</p>" +
                "  </div>" +
                "</body>" +
                "</html>";

        executeWithRetryAndLog(email, subject, htmlContent);
    }

    @Async("emailExecutor")
    public void sendWelcomeEmail(String email, String name) {
        String subject = "Welcome to CYPR";

        String htmlContent = "<html>" +
                "<body style=\"margin:0;padding:0;background-color:#f8fafc;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;color:#334155;\">" +
                "  <div style=\"max-width:540px;margin:40px auto;background-color:#ffffff;border:1px solid #e2e8f0;border-radius:8px;padding:40px;box-shadow:0 4px 6px -1px rgba(0,0,0,0.05);\">" +
                "    <div style=\"text-align:center;margin-bottom:32px;\">" +
                "      <img src=\"" + frontendBaseUrl + "/assets/default-news.jpg\" alt=\"CYPR\" style=\"width:130px;height:auto;display:block;margin:0 auto;\">" +
                "    </div>" +
                "    <h2 style=\"font-size:20px;font-weight:700;color:#0f172a;margin:0 0 16px 0;text-align:center;\">Welcome to CYPR</h2>" +
                "    <p style=\"font-size:14px;color:#334155;line-height:1.6;margin:0 0 24px 0;\">Hello " + name + ",</p>" +
                "    <p style=\"font-size:14px;color:#334155;line-height:1.6;margin:0 0 24px 0;\">Your email address has been successfully verified, and your CYPR account is fully activated.</p>" +
                "    <p style=\"font-size:14px;color:#334155;line-height:1.6;margin:0 0 24px 0;\">Through your secure dashboard, you can now check URLs for suspicious patterns, scan files for potential threat vectors, and verify passwords for known data exposures.</p>" +
                "    <div style=\"text-align:center;margin:32px 0;\">" +
                "      <a href=\"" + frontendBaseUrl + "/login.html\" style=\"display:inline-block;background-color:#0f172a;color:#ffffff;font-weight:600;font-size:14px;text-decoration:none;padding:13px 28px;border-radius:6px;\">Go to Dashboard</a>" +
                "    </div>" +
                "    <div style=\"margin:32px 0;border-top:1px solid #e2e8f0;\"></div>" +
                "    <p style=\"font-size:12px;color:#64748b;line-height:1.5;margin:0 0 16px 0;\">CYPR is engineered to provide privacy-first analytics and local execution validation wherever possible. Feel free to contact support with any inquiries.</p>" +
                "    <p style=\"font-size:12px;color:#94a3b8;margin:0;\">&copy; 2026 CYPR Security. All rights reserved.</p>" +
                "  </div>" +
                "</body>" +
                "</html>";

        executeWithRetryAndLog(email, subject, htmlContent);
    }

    @Async("emailExecutor")
    public void sendContactFormEmail(String adminEmail, String firstName, String lastName, String userEmail, String subjectTopic, String messageContent) {
        String subject = "CYPR Support Inquiry: " + subjectTopic;
        
        String htmlContent = "<html>" +
                "<body style=\"margin:0;padding:0;background-color:#f8fafc;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;color:#334155;\">" +
                "  <div style=\"max-width:580px;margin:40px auto;background-color:#ffffff;border:1px solid #e2e8f0;border-radius:8px;padding:40px;box-shadow:0 4px 6px -1px rgba(0,0,0,0.05);\">" +
                "    <div style=\"text-align:center;margin-bottom:32px;\">" +
                "      <img src=\"" + frontendBaseUrl + "/assets/default-news.jpg\" alt=\"CYPR\" style=\"width:130px;height:auto;display:block;margin:0 auto;\">" +
                "    </div>" +
                "    <h2 style=\"font-size:20px;font-weight:700;color:#0f172a;margin:0 0 24px 0;text-align:center;\">New Contact Form Message</h2>" +
                "    " +
                "    <div style=\"background-color:#f8fafc;border:1px solid #e2e8f0;border-radius:6px;padding:20px;margin-bottom:24px;\">" +
                "      <table style=\"width:100%;border-collapse:collapse;font-size:13px;line-height:1.6;\">" +
                "        <tr>" +
                "          <td style=\"padding:4px 0;font-weight:600;color:#0f172a;width:120px;vertical-align:top;\">Name:</td>" +
                "          <td style=\"padding:4px 0;color:#334155;\">" + firstName + " " + lastName + "</td>" +
                "        </tr>" +
                "        <tr>" +
                "          <td style=\"padding:4px 0;font-weight:600;color:#0f172a;vertical-align:top;\">Sender Email:</td>" +
                "          <td style=\"padding:4px 0;color:#334155;\"><a href=\"mailto:" + userEmail + "\" style=\"color:#2563eb;text-decoration:none;\">" + userEmail + "</a></td>" +
                "        </tr>" +
                "        <tr>" +
                "          <td style=\"padding:4px 0;font-weight:600;color:#0f172a;vertical-align:top;\">Topic:</td>" +
                "          <td style=\"padding:4px 0;color:#334155;\">" + subjectTopic + "</td>" +
                "        </tr>" +
                "        <tr>" +
                "          <td style=\"padding:4px 0;font-weight:600;color:#0f172a;vertical-align:top;\">Received At:</td>" +
                "          <td style=\"padding:4px 0;color:#334155;\">" + java.time.LocalDateTime.now().toString().replace("T", " ").substring(0, 19) + "</td>" +
                "        </tr>" +
                "      </table>" +
                "    </div>" +
                "    " +
                "    <div style=\"background-color:#f8fafc;border:1px solid #e2e8f0;border-radius:6px;padding:20px;margin-bottom:24px;\">" +
                "      <div style=\"font-size:12px;font-weight:700;color:#0f172a;margin-bottom:12px;text-transform:uppercase;letter-spacing:0.5px;\">Message Content</div>" +
                "      <p style=\"font-size:13px;color:#334155;line-height:1.6;white-space:pre-wrap;margin:0;\">" + messageContent + "</p>" +
                "    </div>" +
                "    " +
                "    <div style=\"text-align:center;margin-top:32px;\">" +
                "      <a href=\"mailto:" + userEmail + "?subject=Re: " + subjectTopic + "\" style=\"display:inline-block;background-color:#0f172a;color:#ffffff;font-weight:600;font-size:14px;text-decoration:none;padding:13px 28px;border-radius:6px;\">Reply to Sender</a>" +
                "    </div>" +
                "    " +
                "    <div style=\"margin:32px 0;border-top:1px solid #e2e8f0;\"></div>" +
                "    <p style=\"font-size:11px;color:#64748b;text-align:center;margin:0;\">This is an automated notification from the CYPR contact form service.</p>" +
                "  </div>" +
                "</body>" +
                "</html>";

        executeWithRetryAndLog(adminEmail, subject, htmlContent);
    }

    @Async("emailExecutor")
    public void sendSecurityAlertEmail(String email, String name, String alertType, String ipAddress, String deviceInfo, String location) {
        String subject = "CYPR Security Alert: " + alertType.replace("_", " ");
        String formattedTime = LocalDateTime.now().toString().replace("T", " ").substring(0, 19);

        boolean isHighRisk = alertType.contains("FAILED") || alertType.contains("SUSPICIOUS") || alertType.contains("DEACTIVATED") || alertType.contains("DELETE");
        String cardBg = isHighRisk ? "#fef2f2" : "#f8fafc";
        String cardBorder = isHighRisk ? "1px solid #fecaca" : "1px solid #e2e8f0";
        String textHighlight = isHighRisk ? "#b91c1c" : "#0f172a";

        String htmlContent = "<html>" +
                "<body style=\"margin:0;padding:0;background-color:#f8fafc;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;color:#334155;\">" +
                "  <div style=\"max-width:540px;margin:40px auto;background-color:#ffffff;border:1px solid #e2e8f0;border-radius:8px;padding:40px;box-shadow:0 4px 6px -1px rgba(0,0,0,0.05);\">" +
                "    <div style=\"text-align:center;margin-bottom:32px;\">" +
                "      <img src=\"" + frontendBaseUrl + "/assets/default-news.jpg\" alt=\"CYPR\" style=\"width:130px;height:auto;display:block;margin:0 auto;\">" +
                "    </div>" +
                "    <h2 style=\"font-size:20px;font-weight:700;color:#0f172a;margin:0 0 16px 0;text-align:center;\">Security Advisory</h2>" +
                "    <p style=\"font-size:14px;color:#334155;line-height:1.6;margin:0 0 24px 0;\">Hello " + name + ",</p>" +
                "    <p style=\"font-size:14px;color:#334155;line-height:1.6;margin:0 0 24px 0;\">We detected an account activity or security trigger requiring your attention. Please review the details below:</p>" +
                "    " +
                "    <div style=\"background-color:" + cardBg + ";border:" + cardBorder + ";border-radius:6px;padding:20px;margin:24px 0;\">" +
                "      <div style=\"font-size:13px;font-weight:700;color:" + textHighlight + ";margin-bottom:12px;text-transform:uppercase;letter-spacing:0.5px;\">Event: " + alertType.replace("_", " ") + "</div>" +
                "      <table style=\"width:100%;border-collapse:collapse;font-size:12px;line-height:1.6;\">" +
                "        <tr>" +
                "          <td style=\"padding:3px 0;font-weight:600;color:#0f172a;width:120px;vertical-align:top;\">Time:</td>" +
                "          <td style=\"padding:3px 0;color:#334155;\">" + formattedTime + "</td>" +
                "        </tr>" +
                "        <tr>" +
                "          <td style=\"padding:3px 0;font-weight:600;color:#0f172a;vertical-align:top;\">IP Address:</td>" +
                "          <td style=\"padding:3px 0;color:#334155;\">" + ipAddress + "</td>" +
                "        </tr>" +
                "        <tr>" +
                "          <td style=\"padding:3px 0;font-weight:600;color:#0f172a;vertical-align:top;\">Location:</td>" +
                "          <td style=\"padding:3px 0;color:#334155;\">" + (location != null ? location : "Unknown Location") + "</td>" +
                "        </tr>" +
                "        <tr>" +
                "          <td style=\"padding:3px 0;font-weight:600;color:#0f172a;vertical-align:top;\">Device/Browser:</td>" +
                "          <td style=\"padding:3px 0;color:#334155;\">" + deviceInfo + "</td>" +
                "        </tr>" +
                "      </table>" +
                "    </div>" +
                "    " +
                "    <p style=\"font-size:13px;color:#334155;line-height:1.6;margin:0 0 24px 0;\">If you initiated this change or login activity, no action is required. If you do not recognize this activity, please reset your password and secure your credentials immediately.</p>" +
                "    <div style=\"text-align:center;margin:32px 0;\">" +
                "      <a href=\"" + frontendBaseUrl + "/settings.html\" style=\"display:inline-block;background-color:#0f172a;color:#ffffff;font-weight:600;font-size:14px;text-decoration:none;padding:13px 28px;border-radius:6px;\">Account Settings</a>" +
                "    </div>" +
                "    <div style=\"margin:32px 0;border-top:1px solid #e2e8f0;\"></div>" +
                "    <p style=\"font-size:11px;color:#64748b;line-height:1.5;margin:0 0 16px 0;\">CYPR Security operates client-side threat parsing to maintain strict audit integrity and end-to-end telemetry protection.</p>" +
                "    <p style=\"font-size:11px;color:#94a3b8;margin:0;\">&copy; 2026 CYPR Security. All rights reserved.</p>" +
                "  </div>" +
                "</body>" +
                "</html>";

        executeWithRetryAndLog(email, subject, htmlContent);
    }

    // ── INTERNAL HTTP SENDER & RETRY ENGINE ───────────────────────────────────

    private void executeWithRetryAndLog(String to, String subject, String htmlContent) {
        int maxRetries = 3;
        int attempt = 0;
        boolean success = false;
        String lastError = null;

        if (apiKey == null || apiKey.isBlank()) {
            // Extract the actionable link from html for local testing convenience
            String actionLink = extractFirstHref(htmlContent);

            log.info("");
            log.info("--- CYPR MOCK EMAIL (No API Key - Dev Mode) ---");
            log.info("To: {}", to);
            log.info("Subject: {}", subject);
            if (actionLink != null) {
                log.info("Action: {}", actionLink);
                log.info("Instructions: Copy the URL above into your browser to test this flow locally.");
            }
            log.info("-------------------------------------------------");
            log.info("");

            saveEmailLog(to, subject, "MOCKED", null);
            return;
        }

        while (attempt < maxRetries && !success) {
            attempt++;
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("api-key", apiKey);

                Map<String, Object> body = new LinkedHashMap<>();
                body.put("sender", Map.of("name", senderName, "email", senderEmail));
                body.put("to", List.of(Map.of("email", to)));
                body.put("subject", subject);
                body.put("htmlContent", htmlContent);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
                ResponseEntity<Map> response = rest.postForEntity(BREVO_API_URL, entity, Map.class);

                if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                    success = true;
                    log.info("[EmailService] Email sent successfully via Brevo to {} (Attempt: {})", to, attempt);
                } else {
                    lastError = "Response status: " + response.getStatusCode();
                }
            } catch (Exception e) {
                lastError = e.getMessage();
                log.warn("[EmailService] Attempt {} failed to send email to {}: {}", attempt, to, e.getMessage());
                try {
                    Thread.sleep(1000 * attempt); // Simple backing off
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (success) {
            saveEmailLog(to, subject, "SENT", null);
        } else {
            log.error("[EmailService] All 3 attempts failed to send email to {}. Last error: {}", to, lastError);
            saveEmailLog(to, subject, "FAILED", lastError);
        }
    }

    private void saveEmailLog(String recipient, String subject, String status, String error) {
        try {
            EmailLog logEntry = new EmailLog();
            logEntry.setRecipient(recipient);
            logEntry.setSubject(subject);
            logEntry.setStatus(status);
            logEntry.setErrorMessage(error);
            logEntry.setSentAt(LocalDateTime.now());
            emailLogRepository.save(logEntry);
        } catch (Exception e) {
            log.warn("[EmailService] Audit log persistence failed: {}", e.getMessage());
        }
    }

    /**
     * Extracts the first href URL from an HTML string.
     * Used in dev/mock mode to print the actionable link to the console.
     */
    private String extractFirstHref(String html) {
        if (html == null) return null;
        int idx = html.indexOf("href=\"");
        if (idx == -1) return null;
        int start = idx + 6;
        int end = html.indexOf("\"", start);
        if (end == -1) return null;
        return html.substring(start, end);
    }
}
