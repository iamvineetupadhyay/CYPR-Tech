package com.cypr.modules.billing.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.billing.dto.PaymentRequestDTO;
import com.cypr.modules.billing.dto.PaymentResponseDTO;
import com.cypr.modules.billing.service.PaymentService;
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
@RequestMapping("/api/v1/billing/payments")
@Tag(name = "Payment API", description = "Endpoints for managing Payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all Payments", description = "Retrieve a paginated list of Payments")
    public ResponseEntity<BaseResponse<Page<PaymentResponseDTO>>> getAll(Pageable pageable) {
        Page<PaymentResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Payment by ID")
    public ResponseEntity<BaseResponse<PaymentResponseDTO>> getById(@PathVariable UUID id) {
        PaymentResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Payment")
    public ResponseEntity<BaseResponse<PaymentResponseDTO>> create(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        PaymentResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Payment")
    public ResponseEntity<BaseResponse<PaymentResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody PaymentRequestDTO requestDTO) {
        PaymentResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Payment")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
