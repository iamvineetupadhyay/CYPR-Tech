package com.cypr.exception;

public class BusinessException extends RuntimeException {
    
    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "INTERNAL_ERROR";
    }

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
