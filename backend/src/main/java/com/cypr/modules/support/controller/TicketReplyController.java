package com.cypr.modules.support.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.support.dto.TicketReplyRequestDTO;
import com.cypr.modules.support.dto.TicketReplyResponseDTO;
import com.cypr.modules.support.service.TicketReplyService;
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
@RequestMapping("/api/v1/support/ticket-replies")
@Tag(name = "TicketReply API", description = "Endpoints for managing TicketReplys")
public class TicketReplyController {

    private final TicketReplyService service;

    public TicketReplyController(TicketReplyService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all TicketReplys", description = "Retrieve a paginated list of TicketReplys")
    public ResponseEntity<BaseResponse<Page<TicketReplyResponseDTO>>> getAll(Pageable pageable) {
        Page<TicketReplyResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get TicketReply by ID")
    public ResponseEntity<BaseResponse<TicketReplyResponseDTO>> getById(@PathVariable UUID id) {
        TicketReplyResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new TicketReply")
    public ResponseEntity<BaseResponse<TicketReplyResponseDTO>> create(@Valid @RequestBody TicketReplyRequestDTO requestDTO) {
        TicketReplyResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update TicketReply")
    public ResponseEntity<BaseResponse<TicketReplyResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody TicketReplyRequestDTO requestDTO) {
        TicketReplyResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete TicketReply")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
