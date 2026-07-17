package com.cypr.modules.security.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.security.dto.SessionRequestDTO;
import com.cypr.modules.security.dto.SessionResponseDTO;
import com.cypr.modules.security.service.SessionService;
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
@RequestMapping("/api/v1/security/sessions")
@Tag(name = "Session API", description = "Endpoints for managing Sessions")
public class SessionController {

    private final SessionService service;
    private final com.cypr.modules.security.service.StepUpAuthService stepUpAuthService;

    public SessionController(
            SessionService service,
            com.cypr.modules.security.service.StepUpAuthService stepUpAuthService) {
        this.service = service;
        this.stepUpAuthService = stepUpAuthService;
    }

    @PostMapping("/{id}/revoke")
    @Operation(summary = "Revoke Session (Requires Elevated Token)", description = "Revokes a session and instantly purges any elevated token associated with it.")
    public ResponseEntity<BaseResponse<Void>> revokeSession(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Elevated-Token", required = false) String elevatedToken,
            jakarta.servlet.http.HttpServletRequest request) {

        String currentSessionId = (String) request.getAttribute("authenticatedSessionId");
        stepUpAuthService.validateElevatedToken(elevatedToken, currentSessionId);

        // Revoke session state in DB
        service.delete(id);

        // A5: Instantly purge elevated token tied to the target revoked session ID
        stepUpAuthService.invalidateElevatedSession(id.toString());

        return ResponseEntity.ok(BaseResponse.success("Session revoked successfully", null));
    }

    @GetMapping
    @Operation(summary = "Get all Sessions", description = "Retrieve a paginated list of Sessions")
    public ResponseEntity<BaseResponse<Page<SessionResponseDTO>>> getAll(Pageable pageable) {
        Page<SessionResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Session by ID")
    public ResponseEntity<BaseResponse<SessionResponseDTO>> getById(@PathVariable UUID id) {
        SessionResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Session")
    public ResponseEntity<BaseResponse<SessionResponseDTO>> create(@Valid @RequestBody SessionRequestDTO requestDTO) {
        SessionResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Session")
    public ResponseEntity<BaseResponse<SessionResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody SessionRequestDTO requestDTO) {
        SessionResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Session (Deprecated — use /revoke)")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
