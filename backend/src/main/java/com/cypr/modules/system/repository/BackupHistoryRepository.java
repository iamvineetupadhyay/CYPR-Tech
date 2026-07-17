package com.cypr.modules.system.repository;

import com.cypr.modules.system.entity.BackupHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BackupHistoryRepository extends JpaRepository<BackupHistory, UUID>, JpaSpecificationExecutor<BackupHistory> {

    Optional<BackupHistory> findByFileName(String fileName);

    Page<BackupHistory> findByStatus(String status, Pageable pageable);
}
