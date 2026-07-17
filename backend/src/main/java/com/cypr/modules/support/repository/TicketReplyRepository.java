package com.cypr.modules.support.repository;

import com.cypr.modules.support.entity.Ticket;
import com.cypr.modules.support.entity.TicketReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketReplyRepository extends JpaRepository<TicketReply, UUID>, JpaSpecificationExecutor<TicketReply> {

    @EntityGraph(attributePaths = {"user", "ticket"})
    Page<TicketReply> findByTicket(Ticket ticket, Pageable pageable);
}
