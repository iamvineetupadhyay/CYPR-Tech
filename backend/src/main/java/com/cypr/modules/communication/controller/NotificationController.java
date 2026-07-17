package com.cypr.modules.communication.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.communication.dto.NotificationRequestDTO;
import com.cypr.modules.communication.dto.NotificationResponseDTO;
import com.cypr.modules.communication.service.NotificationService;
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
@RequestMapping("/api/v1/communication/notifications")
@Tag(name = "Notification API", description = "Endpoints for managing Notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all Notifications", description = "Retrieve a paginated list of Notifications")
    public ResponseEntity<BaseResponse<Page<NotificationResponseDTO>>> getAll(Pageable pageable) {
        Page<NotificationResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Notification by ID")
    public ResponseEntity<BaseResponse<NotificationResponseDTO>> getById(@PathVariable UUID id) {
        NotificationResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Notification")
    public ResponseEntity<BaseResponse<NotificationResponseDTO>> create(@Valid @RequestBody NotificationRequestDTO requestDTO) {
        NotificationResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Notification")
    public ResponseEntity<BaseResponse<NotificationResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody NotificationRequestDTO requestDTO) {
        NotificationResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Notification")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
