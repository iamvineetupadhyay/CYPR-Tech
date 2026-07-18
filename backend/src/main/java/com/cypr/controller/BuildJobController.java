package com.cypr.controller;

import com.cypr.entity.BuildJob;
import com.cypr.repository.BuildJobRepository;
import com.cypr.service.WorkspaceManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/builds")
public class BuildJobController {

    private final BuildJobRepository buildJobRepository;
    private final WorkspaceManagerService workspaceManagerService;

    @Autowired
    public BuildJobController(BuildJobRepository buildJobRepository, WorkspaceManagerService workspaceManagerService) {
        this.buildJobRepository = buildJobRepository;
        this.workspaceManagerService = workspaceManagerService;
    }

    @GetMapping
    public List<BuildJob> getAllBuilds() {
        return buildJobRepository.findAll();
    }

    @PostMapping("/trigger")
    public BuildJob triggerBuild(@RequestParam String repositoryUrl,
                                 @RequestParam String branch,
                                 @RequestParam(required = false) String commitSha,
                                 jakarta.servlet.http.HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return workspaceManagerService.triggerBuild(repositoryUrl, branch, commitSha, ip);
    }
}
