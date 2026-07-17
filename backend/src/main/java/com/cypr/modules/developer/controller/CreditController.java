package com.cypr.modules.developer.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.developer.dto.CreditRequestDTO;
import com.cypr.modules.developer.dto.CreditResponseDTO;
import com.cypr.modules.developer.service.CreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/developer/credits")
@Tag(name = "Credit API", description = "Endpoints for managing Credits")
public class CreditController {

    private final CreditService service;
    private final com.cypr.modules.developer.service.IdempotencyService idempotencyService;
    private final com.cypr.modules.security.service.StepUpAuthService stepUpAuthService;

    public CreditController(
            CreditService service,
            com.cypr.modules.developer.service.IdempotencyService idempotencyService,
            com.cypr.modules.security.service.StepUpAuthService stepUpAuthService) {
        this.service = service;
        this.idempotencyService = idempotencyService;
        this.stepUpAuthService = stepUpAuthService;
    }

    @PostMapping("/adjust")
    @Operation(summary = "Adjust User Credits (Audited & Idempotent)", description = "Adjusts credit balance with financial idempotency and mandatory elevated step-up token")
    public ResponseEntity<BaseResponse<CreditResponseDTO>> adjustCredits(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestHeader(value = "X-Elevated-Token", required = false) String elevatedToken,
            @RequestBody Map<String, Object> body,
            jakarta.servlet.http.HttpServletRequest request) {

        // 1. Step-Up Token Validation (A1, A2 session binding)
        String sessionId = (String) request.getAttribute("authenticatedSessionId");
        stepUpAuthService.validateElevatedToken(elevatedToken, sessionId);

        String rawJson = body.toString();

        // 2. A3 Financial Idempotency Check
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existingRecord = idempotencyService.checkIdempotency(idempotencyKey, rawJson);
            if (existingRecord.isPresent()) {
                // Return cached response without executing balance adjustment
                return ResponseEntity.status(existingRecord.get().getStatusCode())
                        .body(BaseResponse.success("Retrieved cached idempotent result", null));
            }
        }

        Long targetUserId = Long.parseLong(body.get("targetUserId").toString());
        Integer amountDelta = Integer.parseInt(body.get("amountDelta").toString());
        String operation = body.getOrDefault("operation", amountDelta >= 0 ? "GRANT" : "REVOKE").toString();
        String reason = body.getOrDefault("reason", "Admin manual credit adjustment").toString();

        Long adminId = com.cypr.security.SecurityUtils.getCurrentUserId(request);
        String ipAddress = request.getRemoteAddr();
        String correlationId = (String) request.getAttribute("correlationId");

        // 3. Perform B1 Transactional Credit Adjustment & Logging
        CreditResponseDTO result = service.adjustCredits(
                targetUserId, adminId != null ? adminId : 0L, amountDelta, operation, reason, ipAddress, correlationId);

        BaseResponse<CreditResponseDTO> response = BaseResponse.success("Credit adjustment completed", result);

        // 4. Save Idempotency Record
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyService.saveIdempotencyRecord(idempotencyKey, rawJson, "SUCCESS", 200);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all Credits", description = "Retrieve a paginated list of Credits")
    public ResponseEntity<BaseResponse<Page<CreditResponseDTO>>> getAll(Pageable pageable) {
        Page<CreditResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Credit by ID")
    public ResponseEntity<BaseResponse<CreditResponseDTO>> getById(@PathVariable UUID id) {
        CreditResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Credit")
    public ResponseEntity<BaseResponse<CreditResponseDTO>> create(@Valid @RequestBody CreditRequestDTO requestDTO) {
        CreditResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Credit")
    public ResponseEntity<BaseResponse<CreditResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody CreditRequestDTO requestDTO) {
        CreditResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Credit")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
