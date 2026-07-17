package com.cypr.modules.users.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.users.dto.RoleRequestDTO;
import com.cypr.modules.users.dto.RoleResponseDTO;
import com.cypr.modules.users.service.RoleService;
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
@RequestMapping("/api/v1/users/roles")
@Tag(name = "Role API", description = "Endpoints for managing Roles")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all Roles", description = "Retrieve a paginated list of Roles")
    public ResponseEntity<BaseResponse<Page<RoleResponseDTO>>> getAll(Pageable pageable) {
        Page<RoleResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Role by ID")
    public ResponseEntity<BaseResponse<RoleResponseDTO>> getById(@PathVariable UUID id) {
        RoleResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Role")
    public ResponseEntity<BaseResponse<RoleResponseDTO>> create(@Valid @RequestBody RoleRequestDTO requestDTO) {
        RoleResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Role")
    public ResponseEntity<BaseResponse<RoleResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody RoleRequestDTO requestDTO) {
        RoleResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Role")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
