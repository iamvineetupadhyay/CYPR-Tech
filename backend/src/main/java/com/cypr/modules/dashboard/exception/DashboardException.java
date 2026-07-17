package com.cypr.modules.dashboard.exception;

import com.cypr.exception.BusinessException;

public class DashboardException extends BusinessException {
    public DashboardException(String message) {
        super(message, "DASHBOARD_ERROR");
    }
}
