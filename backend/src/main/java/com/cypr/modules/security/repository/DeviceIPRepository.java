package com.cypr.modules.security.repository;

import com.cypr.modules.security.entity.Device;
import com.cypr.modules.security.entity.DeviceIP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceIPRepository extends JpaRepository<DeviceIP, UUID>, JpaSpecificationExecutor<DeviceIP> {

    @EntityGraph(attributePaths = {"device", "device.user"})
    Optional<DeviceIP> findByDeviceAndIpAddress(Device device, String ipAddress);

    @EntityGraph(attributePaths = {"device"})
    Page<DeviceIP> findByDevice(Device device, Pageable pageable);
}
