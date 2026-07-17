package com.cypr.modules.communication.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.communication.dto.BulkEmailRequestDTO;
import com.cypr.modules.communication.dto.EmailCampaignResponseDTO;
import com.cypr.modules.communication.dto.EmailDeliveryLogDTO;
import com.cypr.modules.communication.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/bulk")
    public ResponseEntity<BaseResponse<EmailCampaignResponseDTO>> scheduleBulkEmail(@Valid @RequestBody BulkEmailRequestDTO request) {
        return ResponseEntity.ok(BaseResponse.success("Bulk email scheduled", emailService.scheduleBulkEmail(request)));
    }

    @GetMapping("/campaigns")
    public ResponseEntity<BaseResponse<Page<EmailCampaignResponseDTO>>> getCampaignHistory(Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success("Campaign history fetched", emailService.getCampaignHistory(pageable)));
    }

    @GetMapping("/campaigns/{id}/logs")
    public ResponseEntity<BaseResponse<Page<EmailDeliveryLogDTO>>> getCampaignDeliveryLogs(@PathVariable UUID id, Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success("Campaign logs fetched", emailService.getCampaignDeliveryLogs(id, pageable)));
    }

    // This endpoint acts as a manual trigger for the cron job logic for testing purposes
    @PostMapping("/process-queue")
    public ResponseEntity<BaseResponse<Void>> processScheduledEmails() {
        emailService.processScheduledEmails();
        return ResponseEntity.ok(BaseResponse.success("Processed scheduled emails queue", null));
    }
}
