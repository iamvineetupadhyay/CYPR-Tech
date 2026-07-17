package com.cypr.modules.billing.repository;

import com.cypr.entity.User;
import com.cypr.modules.billing.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID>, JpaSpecificationExecutor<Subscription> {

    @EntityGraph(attributePaths = {"user"})
    Page<Subscription> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Optional<Subscription> findByUserAndStatus(User user, String status);
}
