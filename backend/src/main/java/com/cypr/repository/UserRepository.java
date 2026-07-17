package com.cypr.repository;

import com.cypr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // 1. Email se dhoondne ke liye (Purana wala)
    User findByEmail(String email);

    // 2. Username check karne ke liye (Jo humne pehle kiya tha)
    boolean existsByUsername(String username);

    // Ye method dono ko dhoondega
    Optional<User> findByEmailOrUsername(String email, String username);

    // OAuth login ke liye: provider + oauthId se user dhoondo
    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);

    long countBySubscriptionTypeIgnoreCase(String subscriptionType);

    @Query("SELECT COALESCE(SUM(u.credits), 0) FROM User u")
    Long getTotalCreditBalance();

    @Query(value = "SELECT * FROM users u WHERE " +
           "to_tsvector('english', coalesce(u.username, '') || ' ' || coalesce(u.email, '') || ' ' || coalesce(u.full_name, '')) @@ websearch_to_tsquery('english', :query) " +
           "ORDER BY ts_rank(to_tsvector('english', coalesce(u.username, '') || ' ' || coalesce(u.email, '') || ' ' || coalesce(u.full_name, '')), websearch_to_tsquery('english', :query)) DESC",
           countQuery = "SELECT count(*) FROM users u WHERE " +
           "to_tsvector('english', coalesce(u.username, '') || ' ' || coalesce(u.email, '') || ' ' || coalesce(u.full_name, '')) @@ websearch_to_tsquery('english', :query)",
           nativeQuery = true)
    org.springframework.data.domain.Page<User> searchGlobal(@org.springframework.data.repository.query.Param("query") String query, org.springframework.data.domain.Pageable pageable);
}
