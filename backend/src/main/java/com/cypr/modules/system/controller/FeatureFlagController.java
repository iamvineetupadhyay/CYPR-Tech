package com.cypr.modules.system.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.system.dto.FeatureFlagRequestDTO;
import com.cypr.modules.system.dto.FeatureFlagResponseDTO;
import com.cypr.modules.system.service.FeatureFlagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/system/feature-flags")
@Tag(name = "FeatureFlag API", description = "Endpoints for managing FeatureFlags")
public class FeatureFlagController {

    private final FeatureFlagService service;
    private final com.cypr.modules.security.service.StepUpAuthService stepUpAuthService;

    public FeatureFlagController(
            FeatureFlagService service,
            com.cypr.modules.security.service.StepUpAuthService stepUpAuthService) {
        this.service = service;
        this.stepUpAuthService = stepUpAuthService;
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch FeatureFlag State (Requires Elevated Token)", description = "Updates feature flag enable state with JSON patch semantics and Step-Up authentication")
    public ResponseEntity<BaseResponse<FeatureFlagResponseDTO>> patchFeatureFlag(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Elevated-Token", required = false) String elevatedToken,
            @RequestBody java.util.Map<String, Object> patchBody,
            jakarta.servlet.http.HttpServletRequest request) {

        String currentSessionId = (String) request.getAttribute("authenticatedSessionId");
        stepUpAuthService.validateElevatedToken(elevatedToken, currentSessionId);

        FeatureFlagResponseDTO existing = service.getById(id);
        FeatureFlagRequestDTO req = new FeatureFlagRequestDTO();
        req.setFlagKey(existing.getFlagKey());
        req.setDescription(existing.getDescription());

        if (patchBody.containsKey("enabled")) {
            req.setEnabled(Boolean.TRUE.equals(patchBody.get("enabled")));
        } else if (patchBody.containsKey("isEnabled")) {
            req.setEnabled(Boolean.TRUE.equals(patchBody.get("isEnabled")));
        } else {
            req.setEnabled(existing.isEnabled());
        }

        FeatureFlagResponseDTO updated = service.update(id, req);
        return ResponseEntity.ok(BaseResponse.success("Feature flag updated successfully", updated));
    }

    @GetMapping
    @Operation(summary = "Get all FeatureFlags", description = "Retrieve a paginated list of FeatureFlags")
    public ResponseEntity<BaseResponse<Page<FeatureFlagResponseDTO>>> getAll(Pageable pageable) {
        Page<FeatureFlagResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get FeatureFlag by ID")
    public ResponseEntity<BaseResponse<FeatureFlagResponseDTO>> getById(@PathVariable UUID id) {
        FeatureFlagResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new FeatureFlag")
    public ResponseEntity<BaseResponse<FeatureFlagResponseDTO>> create(@Valid @RequestBody FeatureFlagRequestDTO requestDTO) {
        FeatureFlagResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update FeatureFlag")
    public ResponseEntity<BaseResponse<FeatureFlagResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody FeatureFlagRequestDTO requestDTO) {
        FeatureFlagResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete FeatureFlag")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
