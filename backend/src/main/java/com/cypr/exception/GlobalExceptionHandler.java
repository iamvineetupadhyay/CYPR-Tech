package com.cypr.exception;

import com.cypr.common.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusinessException(BusinessException ex) {
        return new ResponseEntity<>(BaseResponse.error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(BaseResponse.error("An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return new ResponseEntity<>(BaseResponse.error(errorMsg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(com.cypr.modules.security.service.StepUpAuthService.StepUpLockedException.class)
    public ResponseEntity<Map<String, Object>> handleStepUpLockedException(com.cypr.modules.security.service.StepUpAuthService.StepUpLockedException ex, jakarta.servlet.http.HttpServletRequest req) {
        Map<String, Object> body = Map.of(
                "success", false,
                "errorCode", "STEP_UP_LOCKED",
                "message", ex.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString(),
                "path", req.getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.LOCKED); // 423
    }

    @ExceptionHandler(com.cypr.modules.security.service.StepUpAuthService.ElevatedTokenSessionMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleElevatedTokenSessionMismatchException(com.cypr.modules.security.service.StepUpAuthService.ElevatedTokenSessionMismatchException ex, jakarta.servlet.http.HttpServletRequest req) {
        Map<String, Object> body = Map.of(
                "success", false,
                "errorCode", "ELEVATED_TOKEN_SESSION_MISMATCH",
                "message", ex.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString(),
                "path", req.getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN); // 403
    }

    @ExceptionHandler(com.cypr.modules.security.service.StepUpAuthService.ElevatedTokenRequiredException.class)
    public ResponseEntity<Map<String, Object>> handleElevatedTokenRequiredException(com.cypr.modules.security.service.StepUpAuthService.ElevatedTokenRequiredException ex, jakarta.servlet.http.HttpServletRequest req) {
        Map<String, Object> body = Map.of(
                "success", false,
                "errorCode", "ELEVATED_AUTH_REQUIRED",
                "message", ex.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString(),
                "path", req.getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN); // 403
    }

    @ExceptionHandler(com.cypr.modules.developer.service.IdempotencyService.IdempotencyKeyConflictException.class)
    public ResponseEntity<Map<String, Object>> handleIdempotencyConflictException(com.cypr.modules.developer.service.IdempotencyService.IdempotencyKeyConflictException ex, jakarta.servlet.http.HttpServletRequest req) {
        Map<String, Object> body = Map.of(
                "success", false,
                "errorCode", "IDEMPOTENCY_KEY_CONFLICT",
                "message", ex.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString(),
                "path", req.getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler({jakarta.persistence.OptimisticLockException.class, org.springframework.dao.OptimisticLockingFailureException.class})
    public ResponseEntity<Map<String, Object>> handleOptimisticLockException(Exception ex, jakarta.servlet.http.HttpServletRequest req) {
        Map<String, Object> body = Map.of(
                "success", false,
                "errorCode", "CONCURRENT_MODIFICATION",
                "message", "This resource was modified by another administrator. Please refresh and retry.",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "path", req.getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        return new ResponseEntity<>(BaseResponse.error("Access denied. You do not have permission to access this resource."), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<BaseResponse<Void>> handleAuthenticationException(org.springframework.security.core.AuthenticationException ex) {
        return new ResponseEntity<>(BaseResponse.error("Authentication failed. " + ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}
