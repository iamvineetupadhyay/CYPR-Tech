package com.cypr.modules.support.entity;

import com.cypr.common.entity.BaseEntity;
import com.cypr.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tickets", indexes = {
    @jakarta.persistence.Index(name = "idx_ticket_status", columnList = "status"),
    @jakarta.persistence.Index(name = "idx_ticket_user", columnList = "user_id")
})
public class Ticket extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String status; // e.g. "OPEN", "IN_PROGRESS", "CLOSED"

    @Column(nullable = false)
    private String priority; // e.g. "LOW", "MEDIUM", "HIGH"

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
