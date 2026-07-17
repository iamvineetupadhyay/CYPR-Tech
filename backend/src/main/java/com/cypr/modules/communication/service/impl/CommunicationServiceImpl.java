package com.cypr.modules.communication.service.impl;

import com.cypr.entity.User;
import com.cypr.exception.BusinessException;
import com.cypr.modules.communication.dto.*;
import com.cypr.modules.communication.entity.Announcement;
import com.cypr.modules.communication.entity.EmailTemplate;
import com.cypr.modules.communication.entity.HomepageBanner;
import com.cypr.modules.communication.entity.Notification;
import com.cypr.modules.communication.repository.AnnouncementRepository;
import com.cypr.modules.communication.repository.EmailTemplateRepository;
import com.cypr.modules.communication.repository.HomepageBannerRepository;
import com.cypr.modules.communication.repository.NotificationRepository;
import com.cypr.modules.communication.service.CommunicationService;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CommunicationServiceImpl implements CommunicationService {

    private final AnnouncementRepository announcementRepository;
    private final HomepageBannerRepository homepageBannerRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public CommunicationServiceImpl(
            AnnouncementRepository announcementRepository,
            HomepageBannerRepository homepageBannerRepository,
            EmailTemplateRepository emailTemplateRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.homepageBannerRepository = homepageBannerRepository;
        this.emailTemplateRepository = emailTemplateRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementResponseDTO> getAnnouncements(Pageable pageable) {
        return announcementRepository.findAll(pageable).map(a -> {
            AnnouncementResponseDTO dto = new AnnouncementResponseDTO();
            dto.setId(a.getId());
            dto.setTitle(a.getTitle());
            dto.setContent(a.getContent());
            dto.setStartsAt(a.getStartsAt());
            dto.setEndsAt(a.getEndsAt());
            dto.setActive(a.isActive());
            return dto;
        });
    }

    @Override
    public AnnouncementResponseDTO createAnnouncement(AnnouncementRequestDTO dto) {
        Announcement a = new Announcement();
        a.setTitle(dto.getTitle());
        a.setContent(dto.getContent());
        a.setStartsAt(dto.getStartsAt());
        a.setEndsAt(dto.getEndsAt());
        a.setActive(dto.isActive());
        a = announcementRepository.save(a);
        
        AnnouncementResponseDTO response = new AnnouncementResponseDTO();
        response.setId(a.getId());
        response.setTitle(a.getTitle());
        return response;
    }

    @Override
    public AnnouncementResponseDTO updateAnnouncement(UUID id, AnnouncementRequestDTO dto) {
        Announcement a = announcementRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Announcement not found"));
        a.setTitle(dto.getTitle());
        a.setContent(dto.getContent());
        a.setStartsAt(dto.getStartsAt());
        a.setEndsAt(dto.getEndsAt());
        a.setActive(dto.isActive());
        a = announcementRepository.save(a);

        AnnouncementResponseDTO response = new AnnouncementResponseDTO();
        response.setId(a.getId());
        response.setTitle(a.getTitle());
        return response;
    }

    @Override
    public void deleteAnnouncement(UUID id) {
        announcementRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HomepageBannerResponseDTO> getHomepageBanners(Pageable pageable) {
        return homepageBannerRepository.findAll(pageable).map(b -> {
            HomepageBannerResponseDTO dto = new HomepageBannerResponseDTO();
            dto.setId(b.getId());
            dto.setImageUrl(b.getImageUrl());
            dto.setLinkUrl(b.getLinkUrl());
            dto.setActive(b.isActive());
            return dto;
        });
    }

    @Override
    public HomepageBannerResponseDTO createBanner(HomepageBannerRequestDTO dto) {
        HomepageBanner b = new HomepageBanner();
        b.setImageUrl(dto.getImageUrl());
        b.setLinkUrl(dto.getLinkUrl());
        b.setActive(dto.isActive());
        b = homepageBannerRepository.save(b);
        
        HomepageBannerResponseDTO response = new HomepageBannerResponseDTO();
        response.setId(b.getId());
        return response;
    }

    @Override
    public HomepageBannerResponseDTO updateBanner(UUID id, HomepageBannerRequestDTO dto) {
        HomepageBanner b = homepageBannerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Banner not found"));
        b.setImageUrl(dto.getImageUrl());
        b.setLinkUrl(dto.getLinkUrl());
        b.setActive(dto.isActive());
        b = homepageBannerRepository.save(b);

        HomepageBannerResponseDTO response = new HomepageBannerResponseDTO();
        response.setId(b.getId());
        return response;
    }

    @Override
    public void deleteBanner(UUID id) {
        homepageBannerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailTemplateResponseDTO> getEmailTemplates(Pageable pageable) {
        return emailTemplateRepository.findAll(pageable).map(t -> {
            EmailTemplateResponseDTO dto = new EmailTemplateResponseDTO();
            dto.setId(t.getId());
            dto.setTemplateCode(t.getTemplateCode());
            dto.setSubject(t.getSubject());
            dto.setBodyHtml(t.getBodyHtml());
            dto.setVariables(t.getVariables());
            return dto;
        });
    }

    @Override
    public EmailTemplateResponseDTO createTemplate(EmailTemplateRequestDTO dto) {
        EmailTemplate t = new EmailTemplate();
        t.setTemplateCode(dto.getTemplateCode());
        t.setSubject(dto.getSubject());
        t.setBodyHtml(dto.getBodyHtml());
        t.setVariables(dto.getVariables());
        t = emailTemplateRepository.save(t);
        
        EmailTemplateResponseDTO response = new EmailTemplateResponseDTO();
        response.setId(t.getId());
        return response;
    }

    @Override
    public EmailTemplateResponseDTO updateTemplate(UUID id, EmailTemplateRequestDTO dto) {
        EmailTemplate t = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Template not found"));
        t.setTemplateCode(dto.getTemplateCode());
        t.setSubject(dto.getSubject());
        t.setBodyHtml(dto.getBodyHtml());
        t.setVariables(dto.getVariables());
        t = emailTemplateRepository.save(t);

        EmailTemplateResponseDTO response = new EmailTemplateResponseDTO();
        response.setId(t.getId());
        return response;
    }

    @Override
    public void deleteTemplate(UUID id) {
        emailTemplateRepository.deleteById(id);
    }

    @Override
    public void sendNotification(NotificationRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setType(dto.getType());
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getUserNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));
        return notificationRepository.findByUser(user, pageable).map(n -> {
            NotificationResponseDTO dto = new NotificationResponseDTO();
            dto.setId(n.getId());
            dto.setTitle(n.getTitle());
            dto.setMessage(n.getMessage());
            dto.setType(n.getType());
            dto.setRead(n.isRead());
            return dto;
        });
    }

    @Override
    public void markNotificationAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
