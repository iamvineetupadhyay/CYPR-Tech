package com.cypr.modules.dashboard.entity;

import com.cypr.audit.Auditable;
import jakarta.persistence.*;

@Entity
@Table(name = "dashboard_metrics")
public class DashboardMetric extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String metricKey;

    @Column(nullable = false)
    private String metricValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(String metricKey) {
        this.metricKey = metricKey;
    }

    public String getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(String metricValue) {
        this.metricValue = metricValue;
    }
}
