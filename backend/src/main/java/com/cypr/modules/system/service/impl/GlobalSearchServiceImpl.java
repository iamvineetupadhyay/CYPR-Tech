package com.cypr.modules.system.service.impl;

import com.cypr.modules.billing.repository.PaymentRepository;
import com.cypr.modules.communication.repository.AnnouncementRepository;
import com.cypr.modules.support.repository.TicketRepository;
import com.cypr.modules.system.dto.GlobalSearchDTO;
import com.cypr.modules.system.repository.SettingRepository;
import com.cypr.modules.system.service.GlobalSearchService;
import com.cypr.repository.SecurityAlertRepository;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class GlobalSearchServiceImpl implements GlobalSearchService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final SecurityAlertRepository securityAlertRepository;
    private final SettingRepository settingRepository;
    private final AnnouncementRepository announcementRepository;

    public GlobalSearchServiceImpl(UserRepository userRepository, TicketRepository ticketRepository,
                                   PaymentRepository paymentRepository, SecurityAlertRepository securityAlertRepository,
                                   SettingRepository settingRepository, AnnouncementRepository announcementRepository) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.securityAlertRepository = securityAlertRepository;
        this.settingRepository = settingRepository;
        this.announcementRepository = announcementRepository;
    }

    @Override
    public Page<GlobalSearchDTO> searchGlobal(String query, Pageable pageable) {
        String tsQuery = formatTsQuery(query);
        List<GlobalSearchDTO> allResults = new ArrayList<>();

        // Fetch from all modules
        userRepository.searchGlobal(tsQuery, pageable).forEach(u -> 
            allResults.add(new GlobalSearchDTO(u.getId().toString(), "USER", u.getUsername(), u.getEmail(), u.getCreatedAt(), "/admin/users/" + u.getId()))
        );

        ticketRepository.searchGlobal(tsQuery, pageable).forEach(t -> 
            allResults.add(new GlobalSearchDTO(t.getId().toString(), "TICKET", t.getSubject(), t.getStatus(), t.getCreatedAt(), "/admin/tickets/" + t.getId()))
        );

        paymentRepository.searchGlobal(tsQuery, pageable).forEach(p -> 
            allResults.add(new GlobalSearchDTO(p.getId().toString(), "PAYMENT", p.getTransactionId(), p.getProvider() + " - " + p.getAmount(), p.getCreatedAt(), "/admin/billing/payments/" + p.getId()))
        );

        securityAlertRepository.searchGlobal(tsQuery, pageable).forEach(s -> 
            allResults.add(new GlobalSearchDTO(s.getId().toString(), "SECURITY_ALERT", s.getAlertType(), s.getIpAddress(), s.getTimestamp(), "/admin/security/" + s.getId()))
        );

        settingRepository.searchGlobal(tsQuery, pageable).forEach(s -> 
            allResults.add(new GlobalSearchDTO(s.getId().toString(), "SETTING", s.getSettingKey(), s.getGroup(), s.getCreatedAt(), "/admin/settings"))
        );

        announcementRepository.searchGlobal(tsQuery, pageable).forEach(a -> 
            allResults.add(new GlobalSearchDTO(a.getId().toString(), "ANNOUNCEMENT", a.getTitle(), a.getContent().substring(0, Math.min(a.getContent().length(), 50)), a.getCreatedAt(), "/admin/communication/announcements"))
        );

        // Sort globally by timestamp descending
        allResults.sort(Comparator.comparing(GlobalSearchDTO::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())));

        // Since we are fetching up to 'size' from each repository, we need to paginate the combined list manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allResults.size());

        List<GlobalSearchDTO> pagedResults = new ArrayList<>();
        if (start < allResults.size()) {
            pagedResults = allResults.subList(start, end);
        }

        return new PageImpl<>(pagedResults, pageable, allResults.size());
    }

    private String formatTsQuery(String query) {
        if (query == null || query.trim().isEmpty()) return "";
        // Convert "admin user" to "admin:* & user:*"
        String[] words = query.trim().split("\\s+");
        StringBuilder tsQuery = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            tsQuery.append(words[i]).append(":*");
            if (i < words.length - 1) {
                tsQuery.append(" & ");
            }
        }
        return tsQuery.toString();
    }
}
