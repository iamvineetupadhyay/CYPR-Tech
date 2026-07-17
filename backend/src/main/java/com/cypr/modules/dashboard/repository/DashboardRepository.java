package com.cypr.modules.dashboard.repository;

import com.cypr.modules.dashboard.entity.DashboardMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DashboardRepository extends JpaRepository<DashboardMetric, Long> {
    Optional<DashboardMetric> findByMetricKey(String metricKey);
}
