package com.cypr.modules.communication.repository;

import com.cypr.modules.communication.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, UUID>, JpaSpecificationExecutor<Announcement> {

    Page<Announcement> findByIsActiveTrueAndStartsAtBeforeAndEndsAtAfter(
            LocalDateTime startsAt, LocalDateTime endsAt, Pageable pageable);

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM announcements a WHERE " +
           "to_tsvector('english', coalesce(a.title, '') || ' ' || coalesce(a.content, '')) @@ websearch_to_tsquery('english', :query) " +
           "ORDER BY ts_rank(to_tsvector('english', coalesce(a.title, '') || ' ' || coalesce(a.content, '')), websearch_to_tsquery('english', :query)) DESC",
           countQuery = "SELECT count(*) FROM announcements a WHERE " +
           "to_tsvector('english', coalesce(a.title, '') || ' ' || coalesce(a.content, '')) @@ websearch_to_tsquery('english', :query)",
           nativeQuery = true)
    Page<Announcement> searchGlobal(@org.springframework.data.repository.query.Param("query") String query, Pageable pageable);
}
