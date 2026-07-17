package com.cypr.modules.developer.repository;

import com.cypr.entity.User;
import com.cypr.modules.developer.entity.ApiKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID>, JpaSpecificationExecutor<ApiKey> {

    @EntityGraph(attributePaths = {"user"})
    Optional<ApiKey> findByKeyHash(String keyHash);

    @EntityGraph(attributePaths = {"user"})
    Page<ApiKey> findByUser(User user, Pageable pageable);
}
