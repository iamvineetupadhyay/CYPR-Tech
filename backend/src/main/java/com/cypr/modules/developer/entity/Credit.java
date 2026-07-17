package com.cypr.modules.developer.entity;

import com.cypr.common.entity.BaseEntity;
import com.cypr.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_credits")
public class Credit extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long balance = 0L;

    @Column(nullable = false)
    private Long totalPurchased = 0L;

    @Column(nullable = false)
    private Long totalConsumed = 0L;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getTotalPurchased() {
        return totalPurchased;
    }

    public void setTotalPurchased(Long totalPurchased) {
        this.totalPurchased = totalPurchased;
    }

    public Long getTotalConsumed() {
        return totalConsumed;
    }

    public void setTotalConsumed(Long totalConsumed) {
        this.totalConsumed = totalConsumed;
    }
}
