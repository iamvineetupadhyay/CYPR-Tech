package com.cypr.modules.security.repository;

import com.cypr.entity.User;
import com.cypr.modules.security.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID>, JpaSpecificationExecutor<Session> {

    @EntityGraph(attributePaths = {"user"})
    Optional<Session> findByToken(String token);

    @EntityGraph(attributePaths = {"user"})
    Page<Session> findByUser(User user, Pageable pageable);
}
