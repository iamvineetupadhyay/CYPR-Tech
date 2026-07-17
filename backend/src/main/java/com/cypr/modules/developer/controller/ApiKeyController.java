package com.cypr.modules.developer.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.developer.dto.ApiKeyRequestDTO;
import com.cypr.modules.developer.dto.ApiKeyResponseDTO;
import com.cypr.modules.developer.service.ApiKeyService;
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
@RequestMapping("/api/v1/developer/api-keys")
@Tag(name = "ApiKey API", description = "Endpoints for managing ApiKeys")
public class ApiKeyController {

    private final ApiKeyService service;
    private final com.cypr.modules.security.service.StepUpAuthService stepUpAuthService;

    public ApiKeyController(
            ApiKeyService service,
            com.cypr.modules.security.service.StepUpAuthService stepUpAuthService) {
        this.service = service;
        this.stepUpAuthService = stepUpAuthService;
    }

    @GetMapping
    @Operation(summary = "Get all ApiKeys", description = "Retrieve a paginated list of ApiKeys with masked key prefix")
    public ResponseEntity<BaseResponse<Page<ApiKeyResponseDTO>>> getAll(Pageable pageable) {
        Page<ApiKeyResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ApiKey by ID")
    public ResponseEntity<BaseResponse<ApiKeyResponseDTO>> getById(@PathVariable UUID id) {
        ApiKeyResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new ApiKey", description = "Generates new API Key. Full secret token is returned ONLY ONCE in response.")
    public ResponseEntity<BaseResponse<ApiKeyResponseDTO>> create(@Valid @RequestBody ApiKeyRequestDTO requestDTO) {
        ApiKeyResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully. Store secret key securely.", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update ApiKey")
    public ResponseEntity<BaseResponse<ApiKeyResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody ApiKeyRequestDTO requestDTO) {
        ApiKeyResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ApiKey (Requires Elevated Token)")
    public ResponseEntity<BaseResponse<Void>> delete(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Elevated-Token", required = false) String elevatedToken,
            jakarta.servlet.http.HttpServletRequest request) {

        String sessionId = (String) request.getAttribute("authenticatedSessionId");
        stepUpAuthService.validateElevatedToken(elevatedToken, sessionId);

        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
