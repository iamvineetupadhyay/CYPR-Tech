package com.cypr.modules.security.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.security.service.StepUpAuthService;
import com.cypr.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/step-up")
@Tag(name = "Step-Up Auth API", description = "Endpoints for elevated 2FA/TOTP authentication")
public class StepUpAuthController {

    private final StepUpAuthService stepUpAuthService;

    public StepUpAuthController(StepUpAuthService stepUpAuthService) {
        this.stepUpAuthService = stepUpAuthService;
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify TOTP for Step-Up Auth", description = "Verifies TOTP code and issues a 5-minute elevated session token bound to active session")
    public ResponseEntity<BaseResponse<Map<String, Object>>> verifyStepUp(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        Long userId = SecurityUtils.getCurrentUserId(request);
        String sessionId = (String) request.getAttribute("authenticatedSessionId");
        if (sessionId == null) {
            sessionId = "session_user_" + (userId != null ? userId : "guest");
        }

        String totpCode = body.get("totpCode");
        String ipAddress = request.getRemoteAddr();
        String correlationId = (String) request.getAttribute("correlationId");

        String elevatedToken = stepUpAuthService.verifyTotpAndIssueElevatedToken(
                sessionId, userId != null ? userId : 0L, totpCode, ipAddress, correlationId);

        Map<String, Object> responseData = Map.of(
                "elevatedToken", elevatedToken,
                "expiresInSeconds", 300,
                "sessionId", sessionId
        );

        return ResponseEntity.ok(BaseResponse.success("Step-up authentication successful", responseData));
    }
}
