package com.cypr.service;

import com.cypr.repository.ScanRepository;
import com.cypr.repository.UserRepository;
import com.cypr.repository.MalwareScanLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatsService {

    @Autowired
    private ScanRepository scanRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MalwareScanLogRepository malwareRepo;

    public Map<String, Long> getGlobalStats() {
        Map<String, Long> stats = new HashMap<>();

        // Database se generic and malware numbers uthana
        long totalGeneric = scanRepo.count();
        long totalMalware = malwareRepo.count();
        stats.put("totalScans", totalGeneric + totalMalware);

        long threatsGeneric = scanRepo.countByResult("Risky");
        long threatsMalware = malwareRepo.countByThreatLevelNot("CLEAN");
        stats.put("threatsBlocked", threatsGeneric + threatsMalware);

        stats.put("activeUsers", userRepo.count());

        return stats;
    }
}