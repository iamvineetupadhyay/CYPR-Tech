package com.cypr.modules.billing.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public class RefundRequestDTO {
    @NotNull(message = "Payment ID is required")
    private UUID paymentId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private String reason;

    @NotNull(message = "Status is required")
    private String status;

    public UUID getPaymentId() { return paymentId; }
    public void setPaymentId(UUID paymentId) { this.paymentId = paymentId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
