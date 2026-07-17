package com.cypr.modules.support.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.support.dto.TicketReplyRequestDTO;
import com.cypr.modules.support.dto.TicketReplyResponseDTO;
import com.cypr.modules.support.entity.TicketReply;
import com.cypr.modules.support.mapper.TicketReplyMapper;
import com.cypr.modules.support.repository.TicketReplyRepository;
import com.cypr.modules.support.service.TicketReplyService;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import com.cypr.modules.support.entity.Ticket;
import com.cypr.modules.support.repository.TicketRepository;
import com.cypr.security.HtmlSanitizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class TicketReplyServiceImpl implements TicketReplyService {

    private final TicketReplyRepository repository;
    private final TicketReplyMapper mapper;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public TicketReplyServiceImpl(TicketReplyRepository repository, TicketReplyMapper mapper, UserRepository userRepository, TicketRepository ticketRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketReplyResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketReplyResponseDTO getById(UUID id) {
        TicketReply entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("TicketReply not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public TicketReplyResponseDTO create(TicketReplyRequestDTO requestDTO) {
        TicketReply entity = mapper.toEntity(requestDTO);
        entity.setMessage(HtmlSanitizer.sanitize(entity.getMessage()));
        if (requestDTO.getUserId() != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        if (requestDTO.getTicketId() != null) {
            Ticket ticket = ticketRepository.findById(requestDTO.getTicketId())
                .orElseThrow(() -> new BusinessException("Ticket not found"));
            entity.setTicket(ticket);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public TicketReplyResponseDTO update(UUID id, TicketReplyRequestDTO requestDTO) {
        TicketReply entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("TicketReply not found with id: " + id));
        entity.setMessage(HtmlSanitizer.sanitize(requestDTO.getMessage()));
        if (requestDTO.getUserId() != null && (entity.getUser() == null || !entity.getUser().getId().equals(requestDTO.getUserId()))) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        if (requestDTO.getTicketId() != null && (entity.getTicket() == null || !entity.getTicket().getId().equals(requestDTO.getTicketId()))) {
            Ticket ticket = ticketRepository.findById(requestDTO.getTicketId())
                .orElseThrow(() -> new BusinessException("Ticket not found"));
            entity.setTicket(ticket);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        TicketReply entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("TicketReply not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
