package com.cypr.modules.communication.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.communication.dto.EmailTemplateRequestDTO;
import com.cypr.modules.communication.dto.EmailTemplateResponseDTO;
import com.cypr.modules.communication.service.EmailTemplateService;
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
@RequestMapping("/api/v1/communication/email-templates")
@Tag(name = "EmailTemplate API", description = "Endpoints for managing EmailTemplates")
public class EmailTemplateController {

    private final EmailTemplateService service;

    public EmailTemplateController(EmailTemplateService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all EmailTemplates", description = "Retrieve a paginated list of EmailTemplates")
    public ResponseEntity<BaseResponse<Page<EmailTemplateResponseDTO>>> getAll(Pageable pageable) {
        Page<EmailTemplateResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get EmailTemplate by ID")
    public ResponseEntity<BaseResponse<EmailTemplateResponseDTO>> getById(@PathVariable UUID id) {
        EmailTemplateResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new EmailTemplate")
    public ResponseEntity<BaseResponse<EmailTemplateResponseDTO>> create(@Valid @RequestBody EmailTemplateRequestDTO requestDTO) {
        EmailTemplateResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update EmailTemplate")
    public ResponseEntity<BaseResponse<EmailTemplateResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody EmailTemplateRequestDTO requestDTO) {
        EmailTemplateResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete EmailTemplate")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
