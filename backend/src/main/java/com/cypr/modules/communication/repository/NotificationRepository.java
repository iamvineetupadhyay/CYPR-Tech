package com.cypr.modules.communication.repository;

import com.cypr.entity.User;
import com.cypr.modules.communication.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID>, JpaSpecificationExecutor<Notification> {

    @EntityGraph(attributePaths = {"user"})
    Page<Notification> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Notification> findByUserAndIsRead(User user, boolean isRead, Pageable pageable);
}
