package com.cypr.modules.developer.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class CreditResponseDTO {

    private UUID id;
    private Long userId;
    private Long balance;
    private Long totalPurchased;
    private Long totalConsumed;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBalance() { return balance; }
    public void setBalance(Long balance) { this.balance = balance; }
    public Long getTotalPurchased() { return totalPurchased; }
    public void setTotalPurchased(Long totalPurchased) { this.totalPurchased = totalPurchased; }
    public Long getTotalConsumed() { return totalConsumed; }
    public void setTotalConsumed(Long totalConsumed) { this.totalConsumed = totalConsumed; }
}
