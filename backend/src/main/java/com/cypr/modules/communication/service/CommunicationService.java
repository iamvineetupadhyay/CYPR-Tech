package com.cypr.modules.communication.service;

import com.cypr.modules.communication.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface CommunicationService {
    
    // Announcements
    Page<AnnouncementResponseDTO> getAnnouncements(Pageable pageable);
    AnnouncementResponseDTO createAnnouncement(AnnouncementRequestDTO dto);
    AnnouncementResponseDTO updateAnnouncement(UUID id, AnnouncementRequestDTO dto);
    void deleteAnnouncement(UUID id);

    // Banners
    Page<HomepageBannerResponseDTO> getHomepageBanners(Pageable pageable);
    HomepageBannerResponseDTO createBanner(HomepageBannerRequestDTO dto);
    HomepageBannerResponseDTO updateBanner(UUID id, HomepageBannerRequestDTO dto);
    void deleteBanner(UUID id);

    // Templates
    Page<EmailTemplateResponseDTO> getEmailTemplates(Pageable pageable);
    EmailTemplateResponseDTO createTemplate(EmailTemplateRequestDTO dto);
    EmailTemplateResponseDTO updateTemplate(UUID id, EmailTemplateRequestDTO dto);
    void deleteTemplate(UUID id);

    // Notifications
    void sendNotification(NotificationRequestDTO dto);
    Page<NotificationResponseDTO> getUserNotifications(Long userId, Pageable pageable);
    void markNotificationAsRead(UUID id);
}
