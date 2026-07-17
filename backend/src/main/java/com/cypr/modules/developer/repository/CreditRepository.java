package com.cypr.modules.developer.repository;

import com.cypr.entity.User;
import com.cypr.modules.developer.entity.Credit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditRepository extends JpaRepository<Credit, UUID>, JpaSpecificationExecutor<Credit> {

    @EntityGraph(attributePaths = {"user"})
    Optional<Credit> findByUser(User user);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(c.balance), 0) FROM Credit c")
    Long getTotalActiveCredits();
}
