package com.cypr.repository;

import com.cypr.entity.VerificationToken;
import com.cypr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByTokenHash(String tokenHash);
    
    // Find active (unused and not expired) tokens for a user
    List<VerificationToken> findByUserAndUsedFalse(User user);

    @Transactional
    void deleteByUser(User user);
}
