package com.cypr.modules.system.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.system.dto.BackupHistoryRequestDTO;
import com.cypr.modules.system.dto.BackupHistoryResponseDTO;
import com.cypr.modules.system.service.BackupHistoryService;
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
@RequestMapping("/api/v1/system/backup-histories")
@Tag(name = "BackupHistory API", description = "Endpoints for managing BackupHistorys")
public class BackupHistoryController {

    private final BackupHistoryService service;

    public BackupHistoryController(BackupHistoryService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all BackupHistorys", description = "Retrieve a paginated list of BackupHistorys")
    public ResponseEntity<BaseResponse<Page<BackupHistoryResponseDTO>>> getAll(Pageable pageable) {
        Page<BackupHistoryResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get BackupHistory by ID")
    public ResponseEntity<BaseResponse<BackupHistoryResponseDTO>> getById(@PathVariable UUID id) {
        BackupHistoryResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new BackupHistory")
    public ResponseEntity<BaseResponse<BackupHistoryResponseDTO>> create(@Valid @RequestBody BackupHistoryRequestDTO requestDTO) {
        BackupHistoryResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update BackupHistory")
    public ResponseEntity<BaseResponse<BackupHistoryResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody BackupHistoryRequestDTO requestDTO) {
        BackupHistoryResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete BackupHistory")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
