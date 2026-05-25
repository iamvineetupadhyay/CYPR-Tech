package com.cypr.repository;

import com.cypr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Email se dhoondne ke liye (Purana wala)
    User findByEmail(String email);

    // 2. Username check karne ke liye (Jo humne pehle kiya tha)
    boolean existsByUsername(String username);

    // Ye method dono ko dhoondega
    Optional<User> findByEmailOrUsername(String email, String username);
}

