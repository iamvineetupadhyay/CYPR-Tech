package com.cypr.controller;

import com.cypr.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*") // Taaki frontend isse connect kar sake
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/global")
    public Map<String, Long> getGlobalStats() {
        return statsService.getGlobalStats();
    }
}