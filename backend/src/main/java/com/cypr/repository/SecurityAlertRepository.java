package com.cypr.repository;

import com.cypr.entity.SecurityAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface SecurityAlertRepository extends JpaRepository<SecurityAlert, Long> {
    List<SecurityAlert> findByUserIdOrderByTimestampDesc(Long userId);

    @Transactional
    void deleteByUserId(Long userId);

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM security_alerts s WHERE " +
           "to_tsvector('english', coalesce(s.alert_type, '') || ' ' || coalesce(s.ip_address, '') || ' ' || coalesce(s.device_info, '')) @@ websearch_to_tsquery('english', :query) " +
           "ORDER BY ts_rank(to_tsvector('english', coalesce(s.alert_type, '') || ' ' || coalesce(s.ip_address, '') || ' ' || coalesce(s.device_info, '')), websearch_to_tsquery('english', :query)) DESC",
           countQuery = "SELECT count(*) FROM security_alerts s WHERE " +
           "to_tsvector('english', coalesce(s.alert_type, '') || ' ' || coalesce(s.ip_address, '') || ' ' || coalesce(s.device_info, '')) @@ websearch_to_tsquery('english', :query)",
           nativeQuery = true)
    org.springframework.data.domain.Page<SecurityAlert> searchGlobal(@org.springframework.data.repository.query.Param("query") String query, org.springframework.data.domain.Pageable pageable);
}
