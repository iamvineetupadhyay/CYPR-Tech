package com.cypr.modules.support.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.support.dto.TicketRequestDTO;
import com.cypr.modules.support.dto.TicketResponseDTO;
import com.cypr.modules.support.entity.Ticket;
import com.cypr.modules.support.mapper.TicketMapper;
import com.cypr.security.HtmlSanitizer;
import com.cypr.modules.support.repository.TicketRepository;
import com.cypr.modules.support.service.TicketService;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository repository;
    private final TicketMapper mapper;
    private final UserRepository userRepository;

    public TicketServiceImpl(TicketRepository repository, TicketMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponseDTO getById(UUID id) {
        Ticket entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Ticket not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public TicketResponseDTO create(TicketRequestDTO requestDTO) {
        Ticket entity = mapper.toEntity(requestDTO);
        entity.setSubject(HtmlSanitizer.sanitize(entity.getSubject()));
        entity.setDescription(HtmlSanitizer.sanitize(entity.getDescription()));
        if (requestDTO.getUserId() != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public TicketResponseDTO update(UUID id, TicketRequestDTO requestDTO) {
        Ticket entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Ticket not found with id: " + id));
        entity.setSubject(HtmlSanitizer.sanitize(requestDTO.getSubject()));
        entity.setDescription(HtmlSanitizer.sanitize(requestDTO.getDescription()));
        entity.setStatus(requestDTO.getStatus());
        entity.setPriority(requestDTO.getPriority());
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
        Ticket entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Ticket not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
