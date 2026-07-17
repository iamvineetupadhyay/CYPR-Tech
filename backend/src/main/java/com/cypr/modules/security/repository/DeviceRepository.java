package com.cypr.modules.security.repository;

import com.cypr.entity.User;
import com.cypr.modules.security.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID>, JpaSpecificationExecutor<Device> {

    @EntityGraph(attributePaths = {"user"})
    Optional<Device> findByFingerprint(String fingerprint);

    @EntityGraph(attributePaths = {"user"})
    Page<Device> findByUser(User user, Pageable pageable);
}
