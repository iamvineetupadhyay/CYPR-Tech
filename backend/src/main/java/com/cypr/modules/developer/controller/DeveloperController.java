package com.cypr.modules.developer.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.developer.dto.WebhookEndpointRequestDTO;
import com.cypr.modules.developer.dto.WebhookEndpointResponseDTO;
import com.cypr.modules.developer.dto.WebhookEventLogDTO;
import com.cypr.modules.developer.service.DeveloperService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/developer/webhooks")
public class DeveloperController {

    private final DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<WebhookEndpointResponseDTO>> registerWebhook(@Valid @RequestBody WebhookEndpointRequestDTO request) {
        return ResponseEntity.ok(BaseResponse.success("Webhook registered", developerService.registerWebhook(request)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<Page<WebhookEndpointResponseDTO>>> getUserWebhooks(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success("User webhooks fetched", developerService.getUserWebhooks(userId, pageable)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<WebhookEndpointResponseDTO>> updateWebhookStatus(@PathVariable UUID id, @RequestParam boolean isActive) {
        return ResponseEntity.ok(BaseResponse.success("Webhook status updated", developerService.updateWebhookStatus(id, isActive)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteWebhook(@PathVariable UUID id) {
        developerService.deleteWebhook(id);
        return ResponseEntity.ok(BaseResponse.success("Webhook deleted", null));
    }

    @PostMapping("/{id}/mock-trigger")
    public ResponseEntity<BaseResponse<Void>> triggerMockWebhook(@PathVariable UUID id, @RequestParam String eventType, @RequestBody String payload) {
        developerService.triggerMockWebhook(id, eventType, payload);
        return ResponseEntity.ok(BaseResponse.success("Mock webhook triggered", null));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<BaseResponse<Page<WebhookEventLogDTO>>> getWebhookLogs(@PathVariable UUID id, Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success("Webhook logs fetched", developerService.getWebhookLogs(id, pageable)));
    }
}
