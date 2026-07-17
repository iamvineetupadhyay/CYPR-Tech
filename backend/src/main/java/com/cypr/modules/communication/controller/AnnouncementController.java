package com.cypr.modules.communication.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.communication.dto.AnnouncementRequestDTO;
import com.cypr.modules.communication.dto.AnnouncementResponseDTO;
import com.cypr.modules.communication.service.AnnouncementService;
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
@RequestMapping("/api/v1/communication/announcements")
@Tag(name = "Announcement API", description = "Endpoints for managing Announcements")
public class AnnouncementController {

    private final AnnouncementService service;

    public AnnouncementController(AnnouncementService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all Announcements", description = "Retrieve a paginated list of Announcements")
    public ResponseEntity<BaseResponse<Page<AnnouncementResponseDTO>>> getAll(Pageable pageable) {
        Page<AnnouncementResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Announcement by ID")
    public ResponseEntity<BaseResponse<AnnouncementResponseDTO>> getById(@PathVariable UUID id) {
        AnnouncementResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Announcement")
    public ResponseEntity<BaseResponse<AnnouncementResponseDTO>> create(@Valid @RequestBody AnnouncementRequestDTO requestDTO) {
        AnnouncementResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Announcement")
    public ResponseEntity<BaseResponse<AnnouncementResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody AnnouncementRequestDTO requestDTO) {
        AnnouncementResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Announcement")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
