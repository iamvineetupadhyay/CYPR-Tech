package com.cypr.modules.communication.repository;

import com.cypr.modules.communication.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, UUID>, JpaSpecificationExecutor<EmailTemplate> {

    Optional<EmailTemplate> findByTemplateCode(String templateCode);
}
