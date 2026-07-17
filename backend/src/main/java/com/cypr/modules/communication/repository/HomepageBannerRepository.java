package com.cypr.modules.communication.repository;

import com.cypr.modules.communication.entity.HomepageBanner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HomepageBannerRepository extends JpaRepository<HomepageBanner, UUID>, JpaSpecificationExecutor<HomepageBanner> {

    Page<HomepageBanner> findByIsActiveTrue(Pageable pageable);
}
