package com.cypr.modules.support.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.support.dto.TicketRequestDTO;
import com.cypr.modules.support.dto.TicketResponseDTO;
import com.cypr.modules.support.service.TicketService;
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
@RequestMapping("/api/v1/support/tickets")
@Tag(name = "Ticket API", description = "Endpoints for managing Tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all Tickets", description = "Retrieve a paginated list of Tickets")
    public ResponseEntity<BaseResponse<Page<TicketResponseDTO>>> getAll(Pageable pageable) {
        Page<TicketResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Ticket by ID")
    public ResponseEntity<BaseResponse<TicketResponseDTO>> getById(@PathVariable UUID id) {
        TicketResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Ticket")
    public ResponseEntity<BaseResponse<TicketResponseDTO>> create(@Valid @RequestBody TicketRequestDTO requestDTO) {
        TicketResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Ticket")
    public ResponseEntity<BaseResponse<TicketResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody TicketRequestDTO requestDTO) {
        TicketResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Ticket")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
