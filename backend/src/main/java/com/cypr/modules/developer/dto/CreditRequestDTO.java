package com.cypr.modules.developer.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class CreditRequestDTO {

    @NotNull
    private Long userId;

    @NotNull
    private Long balance;

    @NotNull
    private Long totalPurchased;

    @NotNull
    private Long totalConsumed;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBalance() { return balance; }
    public void setBalance(Long balance) { this.balance = balance; }
    public Long getTotalPurchased() { return totalPurchased; }
    public void setTotalPurchased(Long totalPurchased) { this.totalPurchased = totalPurchased; }
    public Long getTotalConsumed() { return totalConsumed; }
    public void setTotalConsumed(Long totalConsumed) { this.totalConsumed = totalConsumed; }
}
