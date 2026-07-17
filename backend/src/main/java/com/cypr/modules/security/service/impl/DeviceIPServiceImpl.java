package com.cypr.modules.security.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.security.dto.DeviceIPRequestDTO;
import com.cypr.modules.security.dto.DeviceIPResponseDTO;
import com.cypr.modules.security.entity.DeviceIP;
import com.cypr.modules.security.mapper.DeviceIPMapper;
import com.cypr.modules.security.repository.DeviceIPRepository;
import com.cypr.modules.security.service.DeviceIPService;
import com.cypr.modules.security.entity.Device;
import com.cypr.modules.security.repository.DeviceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeviceIPServiceImpl implements DeviceIPService {

    private final DeviceIPRepository repository;
    private final DeviceIPMapper mapper;
    private final DeviceRepository deviceRepository;

    public DeviceIPServiceImpl(DeviceIPRepository repository, DeviceIPMapper mapper, DeviceRepository deviceRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.deviceRepository = deviceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeviceIPResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceIPResponseDTO getById(UUID id) {
        DeviceIP entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("DeviceIP not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public DeviceIPResponseDTO create(DeviceIPRequestDTO requestDTO) {
        DeviceIP entity = mapper.toEntity(requestDTO);
        if (requestDTO.getDeviceId() != null) {
            Device device = deviceRepository.findById(requestDTO.getDeviceId())
                .orElseThrow(() -> new BusinessException("Device not found"));
            entity.setDevice(device);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public DeviceIPResponseDTO update(UUID id, DeviceIPRequestDTO requestDTO) {
        DeviceIP entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("DeviceIP not found with id: " + id));
        entity.setIpAddress(requestDTO.getIpAddress());
        entity.setLastSeenAt(requestDTO.getLastSeenAt());
        if (requestDTO.getDeviceId() != null && (entity.getDevice() == null || !entity.getDevice().getId().equals(requestDTO.getDeviceId()))) {
            Device device = deviceRepository.findById(requestDTO.getDeviceId())
                .orElseThrow(() -> new BusinessException("Device not found"));
            entity.setDevice(device);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        DeviceIP entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("DeviceIP not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
