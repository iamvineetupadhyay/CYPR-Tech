package com.cypr.modules.security.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.security.dto.SecurityLogRequestDTO;
import com.cypr.modules.security.dto.SecurityLogResponseDTO;
import com.cypr.modules.security.service.SecurityLogService;
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
@RequestMapping("/api/v1/security/security-logs")
@Tag(name = "SecurityLog API", description = "Endpoints for managing SecurityLogs")
public class SecurityLogController {

    private final SecurityLogService service;

    public SecurityLogController(SecurityLogService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all SecurityLogs", description = "Retrieve a paginated list of SecurityLogs")
    public ResponseEntity<BaseResponse<Page<SecurityLogResponseDTO>>> getAll(Pageable pageable) {
        Page<SecurityLogResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get SecurityLog by ID")
    public ResponseEntity<BaseResponse<SecurityLogResponseDTO>> getById(@PathVariable UUID id) {
        SecurityLogResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new SecurityLog")
    public ResponseEntity<BaseResponse<SecurityLogResponseDTO>> create(@Valid @RequestBody SecurityLogRequestDTO requestDTO) {
        SecurityLogResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update SecurityLog")
    public ResponseEntity<BaseResponse<SecurityLogResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody SecurityLogRequestDTO requestDTO) {
        SecurityLogResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete SecurityLog")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
