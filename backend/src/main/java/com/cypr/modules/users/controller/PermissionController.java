package com.cypr.modules.users.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.users.dto.PermissionRequestDTO;
import com.cypr.modules.users.dto.PermissionResponseDTO;
import com.cypr.modules.users.service.PermissionService;
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
@RequestMapping("/api/v1/users/permissions")
@Tag(name = "Permission API", description = "Endpoints for managing Permissions")
public class PermissionController {

    private final PermissionService service;

    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all Permissions", description = "Retrieve a paginated list of Permissions")
    public ResponseEntity<BaseResponse<Page<PermissionResponseDTO>>> getAll(Pageable pageable) {
        Page<PermissionResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Permission by ID")
    public ResponseEntity<BaseResponse<PermissionResponseDTO>> getById(@PathVariable UUID id) {
        PermissionResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Permission")
    public ResponseEntity<BaseResponse<PermissionResponseDTO>> create(@Valid @RequestBody PermissionRequestDTO requestDTO) {
        PermissionResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Permission")
    public ResponseEntity<BaseResponse<PermissionResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody PermissionRequestDTO requestDTO) {
        PermissionResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Permission")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
