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
}
