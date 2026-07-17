package com.cypr.modules.support.repository;

import com.cypr.entity.User;
import com.cypr.modules.support.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID>, JpaSpecificationExecutor<Ticket> {

    @EntityGraph(attributePaths = {"user"})
    Page<Ticket> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Ticket> findByStatus(String status, Pageable pageable);

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM tickets t WHERE " +
           "to_tsvector('english', coalesce(t.subject, '') || ' ' || coalesce(t.description, '')) @@ websearch_to_tsquery('english', :query) " +
           "ORDER BY ts_rank(to_tsvector('english', coalesce(t.subject, '') || ' ' || coalesce(t.description, '')), websearch_to_tsquery('english', :query)) DESC",
           countQuery = "SELECT count(*) FROM tickets t WHERE " +
           "to_tsvector('english', coalesce(t.subject, '') || ' ' || coalesce(t.description, '')) @@ websearch_to_tsquery('english', :query)",
           nativeQuery = true)
    Page<Ticket> searchGlobal(@org.springframework.data.repository.query.Param("query") String query, Pageable pageable);
}
