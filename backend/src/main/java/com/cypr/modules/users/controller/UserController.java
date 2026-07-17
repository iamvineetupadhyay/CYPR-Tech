package com.cypr.modules.users.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.users.dto.UserProfileResponseDTO;
import com.cypr.modules.users.dto.UserRequestDTO;
import com.cypr.modules.users.dto.UserResponseDTO;
import com.cypr.modules.users.dto.UserSearchCriteriaDTO;
import com.cypr.modules.users.service.UserService;
import com.cypr.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("moduleUserController")
@RequestMapping("/api/v1/users")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
@Tag(name = "User API", description = "Endpoints for managing users, profiles, and exports")
public class UserController {

    private final UserService userService;
    private final com.cypr.modules.security.service.StepUpAuthService stepUpAuthService;
    private final com.cypr.modules.users.repository.UserStatusLogRepository statusLogRepository;
    private final com.cypr.modules.security.repository.AdminAuditLogRepository auditLogRepository;

    public UserController(
            UserService userService,
            com.cypr.modules.security.service.StepUpAuthService stepUpAuthService,
            com.cypr.modules.users.repository.UserStatusLogRepository statusLogRepository,
            com.cypr.modules.security.repository.AdminAuditLogRepository auditLogRepository) {
        this.userService = userService;
        this.stepUpAuthService = stepUpAuthService;
        this.statusLogRepository = statusLogRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    @Operation(summary = "Search Users", description = "Retrieves a paginated list of users with optional filtering and sorting")
    public ResponseEntity<BaseResponse<Page<UserResponseDTO>>> searchUsers(
            UserSearchCriteriaDTO criteria,
            Pageable pageable) {
        Page<UserResponseDTO> users = userService.searchUsers(criteria, pageable);
        return ResponseEntity.ok(BaseResponse.success("Users retrieved", users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User by ID", description = "Retrieves core details for a specific user")
    public ResponseEntity<BaseResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(BaseResponse.success("User retrieved", user));
    }

    @GetMapping("/{id}/profile")
    @Operation(summary = "Get Full User Profile", description = "Retrieves user details along with credit balance, device history, session history, and login logs")
    public ResponseEntity<BaseResponse<UserProfileResponseDTO>> getUserProfile(@PathVariable Long id) {
        UserProfileResponseDTO profile = userService.getUserProfile(id);
        return ResponseEntity.ok(BaseResponse.success("User profile retrieved", profile));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update User Status with Reason (Requires Elevated Token)", description = "Updates user status (ACTIVE/SUSPENDED/DISABLED) with mandatory audit reason logging")
    public ResponseEntity<BaseResponse<UserResponseDTO>> updateUserStatus(
            @PathVariable Long id,
            @RequestHeader(value = "X-Elevated-Token", required = false) String elevatedToken,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        String currentSessionId = (String) request.getAttribute("authenticatedSessionId");
        stepUpAuthService.validateElevatedToken(elevatedToken, currentSessionId);

        String newStatus = body.getOrDefault("status", "ACTIVE");
        String reason = body.getOrDefault("reason", "Admin manual status change");

        UserResponseDTO existing = userService.getUserById(id);
        String previousStatus = existing.isEnabled() ? "ACTIVE" : "DISABLED";

        UserRequestDTO req = new UserRequestDTO();
        req.setName(existing.getName());
        req.setEmail(existing.getEmail());
        req.setUsername(existing.getUsername());
        req.setEnabled("ACTIVE".equalsIgnoreCase(newStatus));
        UserResponseDTO updated = userService.updateUser(id, req);

        Long adminId = SecurityUtils.getCurrentUserId(request);
        String ipAddress = request.getRemoteAddr();
        String correlationId = (String) request.getAttribute("correlationId");

        // Save immutable UserStatusLog (Requirement 5 & A4)
        statusLogRepository.save(new com.cypr.modules.users.entity.UserStatusLog(
                id, adminId != null ? adminId : 0L, previousStatus, newStatus, reason, ipAddress, correlationId));

        // Save immutable AdminAuditLog
        auditLogRepository.save(new com.cypr.modules.security.entity.AdminAuditLog(
                adminId != null ? adminId : 0L, "admin", "USER_STATUS_CHANGE",
                "Changed User #" + id + " status from " + previousStatus + " to " + newStatus + ". Reason: " + reason,
                ipAddress, correlationId));

        return ResponseEntity.ok(BaseResponse.success("User status updated to " + newStatus, updated));
    }

    @PostMapping
    @Operation(summary = "Create User", description = "Creates a new user manually from the admin panel")
    public ResponseEntity<BaseResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO createdUser = userService.createUser(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("User created successfully", createdUser));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update User", description = "Updates an existing user's core details or roles")
    public ResponseEntity<BaseResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Disable User", description = "Soft deletes a user by setting enabled=false")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(BaseResponse.success("User disabled successfully", null));
    }

    @GetMapping("/export")
    @Operation(summary = "Export Users to CSV", description = "Downloads a CSV file of users matching the search criteria")
    public ResponseEntity<byte[]> exportUsersToCsv(UserSearchCriteriaDTO criteria) {
        byte[] csvData = userService.exportUsersToCsv(criteria);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "users_export.csv");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }
}
