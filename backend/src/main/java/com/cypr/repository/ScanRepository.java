package com.cypr.repository;

import com.cypr.entity.ScanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface ScanRepository extends JpaRepository<ScanHistory, Long> {

    // User ki personal history ke liye (Dashboard)
    List<ScanHistory> findByUserIdOrderByTimestampDesc(Long userId);

    // Global stats ke liye (Landing Page)
    long countByResult(String result);

    @Transactional
    void deleteByUserId(Long userId);
}