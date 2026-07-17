package com.cypr.modules.users.repository;

import com.cypr.modules.users.entity.UserStatusLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserStatusLogRepository extends Repository<UserStatusLog, Long> {
    UserStatusLog save(UserStatusLog entity);
    Optional<UserStatusLog> findById(Long id);
    Page<UserStatusLog> findAll(Pageable pageable);
    Page<UserStatusLog> findByUserId(Long userId, Pageable pageable);
}
