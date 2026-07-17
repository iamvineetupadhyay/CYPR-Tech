package com.cypr.modules.security.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.security.dto.ActivityLogRequestDTO;
import com.cypr.modules.security.dto.ActivityLogResponseDTO;
import com.cypr.modules.security.service.ActivityLogService;
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
@RequestMapping("/api/v1/security/activity-logs")
@Tag(name = "ActivityLog API", description = "Endpoints for managing ActivityLogs")
public class ActivityLogController {

    private final ActivityLogService service;

    public ActivityLogController(ActivityLogService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all ActivityLogs", description = "Retrieve a paginated list of ActivityLogs")
    public ResponseEntity<BaseResponse<Page<ActivityLogResponseDTO>>> getAll(Pageable pageable) {
        Page<ActivityLogResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ActivityLog by ID")
    public ResponseEntity<BaseResponse<ActivityLogResponseDTO>> getById(@PathVariable UUID id) {
        ActivityLogResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new ActivityLog")
    public ResponseEntity<BaseResponse<ActivityLogResponseDTO>> create(@Valid @RequestBody ActivityLogRequestDTO requestDTO) {
        ActivityLogResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update ActivityLog")
    public ResponseEntity<BaseResponse<ActivityLogResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody ActivityLogRequestDTO requestDTO) {
        ActivityLogResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ActivityLog")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
