package com.cypr.modules.billing.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.billing.dto.CouponRequestDTO;
import com.cypr.modules.billing.dto.CouponResponseDTO;
import com.cypr.modules.billing.service.CouponService;
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
@RequestMapping("/api/v1/billing/coupons")
@Tag(name = "Coupon API", description = "Endpoints for managing Coupons")
public class CouponController {

    private final CouponService service;

    public CouponController(CouponService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all Coupons", description = "Retrieve a paginated list of Coupons")
    public ResponseEntity<BaseResponse<Page<CouponResponseDTO>>> getAll(Pageable pageable) {
        Page<CouponResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Coupon by ID")
    public ResponseEntity<BaseResponse<CouponResponseDTO>> getById(@PathVariable UUID id) {
        CouponResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Coupon")
    public ResponseEntity<BaseResponse<CouponResponseDTO>> create(@Valid @RequestBody CouponRequestDTO requestDTO) {
        CouponResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Coupon")
    public ResponseEntity<BaseResponse<CouponResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody CouponRequestDTO requestDTO) {
        CouponResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Coupon")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
