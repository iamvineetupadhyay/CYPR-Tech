package com.cypr.modules.security.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.security.dto.DeviceRequestDTO;
import com.cypr.modules.security.dto.DeviceResponseDTO;
import com.cypr.modules.security.entity.Device;
import com.cypr.modules.security.mapper.DeviceMapper;
import com.cypr.modules.security.repository.DeviceRepository;
import com.cypr.modules.security.service.DeviceService;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository repository;
    private final DeviceMapper mapper;
    private final UserRepository userRepository;

    public DeviceServiceImpl(DeviceRepository repository, DeviceMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeviceResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceResponseDTO getById(UUID id) {
        Device entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Device not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public DeviceResponseDTO create(DeviceRequestDTO requestDTO) {
        Device entity = mapper.toEntity(requestDTO);
        if (requestDTO.getUserId() != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public DeviceResponseDTO update(UUID id, DeviceRequestDTO requestDTO) {
        Device entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Device not found with id: " + id));
        entity.setFingerprint(requestDTO.getFingerprint());
        entity.setUserAgent(requestDTO.getUserAgent());
        entity.setOs(requestDTO.getOs());
        entity.setBrowser(requestDTO.getBrowser());
        if (requestDTO.getUserId() != null && (entity.getUser() == null || !entity.getUser().getId().equals(requestDTO.getUserId()))) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        Device entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Device not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
