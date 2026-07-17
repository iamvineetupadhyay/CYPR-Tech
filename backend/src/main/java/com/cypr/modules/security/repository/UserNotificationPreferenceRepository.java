package com.cypr.modules.security.repository;

import com.cypr.modules.security.entity.UserNotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserNotificationPreferenceRepository extends JpaRepository<UserNotificationPreference, UUID> {
    List<UserNotificationPreference> findByUserId(Long userId);
    Optional<UserNotificationPreference> findByUserIdAndCategory(Long userId, String category);
}
