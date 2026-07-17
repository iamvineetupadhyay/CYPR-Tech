package com.cypr.modules.billing.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.billing.dto.SubscriptionRequestDTO;
import com.cypr.modules.billing.dto.SubscriptionResponseDTO;
import com.cypr.modules.billing.service.SubscriptionService;
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
@RequestMapping("/api/v1/billing/subscriptions")
@Tag(name = "Subscription API", description = "Endpoints for managing Subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all Subscriptions", description = "Retrieve a paginated list of Subscriptions")
    public ResponseEntity<BaseResponse<Page<SubscriptionResponseDTO>>> getAll(Pageable pageable) {
        Page<SubscriptionResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Subscription by ID")
    public ResponseEntity<BaseResponse<SubscriptionResponseDTO>> getById(@PathVariable UUID id) {
        SubscriptionResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Subscription")
    public ResponseEntity<BaseResponse<SubscriptionResponseDTO>> create(@Valid @RequestBody SubscriptionRequestDTO requestDTO) {
        SubscriptionResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Subscription")
    public ResponseEntity<BaseResponse<SubscriptionResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody SubscriptionRequestDTO requestDTO) {
        SubscriptionResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Subscription")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
