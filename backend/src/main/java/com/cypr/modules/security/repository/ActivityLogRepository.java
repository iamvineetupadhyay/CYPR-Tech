package com.cypr.modules.security.repository;

import com.cypr.entity.User;
import com.cypr.modules.security.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID>, JpaSpecificationExecutor<ActivityLog> {

    @EntityGraph(attributePaths = {"user"})
    Page<ActivityLog> findByUser(User user, Pageable pageable);

    List<ActivityLog> findByUserId(Long userId);
}
