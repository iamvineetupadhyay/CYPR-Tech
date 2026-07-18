package com.cypr.service;

import com.cypr.entity.BuildJob;
import com.cypr.entity.BuildJobStatus;
import com.cypr.repository.BuildJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WorkspaceManagerService {

    private final BuildJobRepository buildJobRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;
    private final ExecutorService executorService;
    private final Map<String, List<Long>> rateLimits = new java.util.concurrent.ConcurrentHashMap<>();

    @Autowired
    public WorkspaceManagerService(BuildJobRepository buildJobRepository, SimpMessagingTemplate messagingTemplate, EmailService emailService) {
        this.buildJobRepository = buildJobRepository;
        this.messagingTemplate = messagingTemplate;
        this.emailService = emailService;
        
        // Bounded ThreadPoolExecutor: Core size 2, Max size 2, keepAlive 60s, queue capacity 10
        this.executorService = new java.util.concurrent.ThreadPoolExecutor(
            2, 2, 60L, java.util.concurrent.TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10),
            new java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy()
        );
    }

    public BuildJob triggerBuild(String repositoryUrl, String branch, String commitSha, String clientIp) {
        // 1. Sliding Window Rate Limiting (Max 3 build triggers per minute per client IP)
        long now = System.currentTimeMillis();
        List<Long> requestTimes = rateLimits.computeIfAbsent(clientIp, k -> new java.util.concurrent.CopyOnWriteArrayList<>());
        requestTimes.removeIf(t -> now - t > 60000);
        if (requestTimes.size() >= 3) {
            throw new IllegalStateException("Rate limit exceeded. Maximum 3 build triggers per minute allowed.");
        }
        requestTimes.add(now);

        // 2. Strict Whitelist Validations
        if (repositoryUrl == null || branch == null) {
            throw new IllegalArgumentException("Repository URL and branch must not be null.");
        }

        String cleanUrl = repositoryUrl.trim();
        String cleanBranch = branch.trim();
        String cleanCommit = (commitSha != null) ? commitSha.trim() : null;

        // Host Whitelist: github.com and gitlab.com allowed only
        if (!cleanUrl.matches("^https:\\/\\/(github\\.com|gitlab\\.com)\\/[a-zA-Z0-9\\.\\-_]+\\/[a-zA-Z0-9\\.\\-_]+\\.git$")) {
            throw new IllegalArgumentException("Repository URL must be a valid GitHub or GitLab HTTPS clone endpoint.");
        }

        // Branch Validation (no leading dash, safe characters only)
        if (cleanBranch.startsWith("-") || !cleanBranch.matches("^[a-zA-Z0-9\\.\\-_/]+$")) {
            throw new IllegalArgumentException("Branch name must be a valid git branch name and cannot start with a dash.");
        }

        // Commit SHA Validation (optional - strictly 7 to 40 hex characters, no leading dash)
        if (cleanCommit != null && !cleanCommit.isEmpty()) {
            if (cleanCommit.startsWith("-") || !cleanCommit.matches("^[a-f0-9]{7,40}$")) {
                throw new IllegalArgumentException("Commit SHA must be a valid hex digest string (7 to 40 characters) and cannot start with a dash.");
            }
        }

        String jobId = UUID.randomUUID().toString();
        BuildJob job = new BuildJob(jobId, cleanUrl, cleanBranch, cleanCommit, BuildJobStatus.PENDING, LocalDateTime.now());
        buildJobRepository.save(job);

        executorService.submit(() -> {
            executeBuildWorkflow(job);
        });

        return job;
    }

    private void executeBuildWorkflow(BuildJob job) {
        String jobId = job.getJobId();
        job.setStatus(BuildJobStatus.RUNNING);
        buildJobRepository.save(job);
        
        sendStatusUpdate(jobId, BuildJobStatus.RUNNING);

        try {
            File tempDir = new File(System.getProperty("user.dir"), "build-workspace-" + jobId);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            List<String> cloneCmd = List.of("git", "clone", "-b", job.getBranch(), job.getRepositoryUrl(), ".");
            boolean cloneSuccess = executeNativeCommand(jobId, cloneCmd, tempDir);
            
            if (!cloneSuccess) {
                failJob(job, "Git clone command failed.");
                return;
            }

            if (job.getCommitSha() != null && !job.getCommitSha().trim().isEmpty()) {
                List<String> checkoutCmd = List.of("git", "checkout", "--", job.getCommitSha());
                boolean checkoutSuccess = executeNativeCommand(jobId, checkoutCmd, tempDir);
                if (!checkoutSuccess) {
                    failJob(job, "Git checkout commit SHA failed.");
                    return;
                }
            }

            deleteDirectory(tempDir);

            job.setStatus(BuildJobStatus.SUCCESS);
            job.setCompletedAt(LocalDateTime.now());
            buildJobRepository.save(job);
            sendStatusUpdate(jobId, BuildJobStatus.SUCCESS);
            sendLogLine(jobId, "[CYPR BUILD ENGINE] Build completed successfully.");

        } catch (Exception e) {
            failJob(job, "Internal error in build execution: " + e.getMessage());
        }
    }

    private void failJob(BuildJob job, String reason) {
        job.setStatus(BuildJobStatus.FAILED);
        job.setCompletedAt(LocalDateTime.now());
        buildJobRepository.save(job);
        sendStatusUpdate(job.getJobId(), BuildJobStatus.FAILED);
        sendLogLine(job.getJobId(), "[CYPR BUILD ENGINE] ERROR: " + reason);

        // Send automated notification to administrator on failure
        try {
            emailService.sendBuildFailedAlert("vineetk5704@gmail.com", job.getJobId(), maskSecrets(job.getRepositoryUrl()), job.getBranch(), reason);
            sendLogLine(job.getJobId(), "[CYPR BUILD ENGINE] Notification email alert successfully dispatched.");
        } catch (Exception e) {
            sendLogLine(job.getJobId(), "[CYPR BUILD ENGINE] WARNING: Failed to dispatch alert email: " + e.getMessage());
        }
    }

    public boolean executeNativeCommand(String jobId, List<String> command, File workingDirectory) {
        List<String> maskedCommand = new java.util.ArrayList<>();
        for (String arg : command) {
            maskedCommand.add(maskSecrets(arg));
        }
        sendLogLine(jobId, "[CYPR BUILD ENGINE] Running command: " + String.join(" ", maskedCommand));
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(workingDirectory);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sendLogLine(jobId, line);
                }
            }

            int exitCode = process.waitFor();
            sendLogLine(jobId, "[CYPR BUILD ENGINE] Command exit code: " + exitCode);
            return exitCode == 0;
        } catch (Exception e) {
            sendLogLine(jobId, "[CYPR BUILD ENGINE] Exception running command: " + e.getMessage());
            return false;
        }
    }

    private void sendLogLine(String jobId, String logLine) {
        String maskedLine = maskSecrets(logLine);
        messagingTemplate.convertAndSend("/topic/builds/" + jobId, Map.of(
            "type", "LOG",
            "payload", maskedLine
        ));
    }

    private String maskSecrets(String text) {
        if (text == null) return null;
        // Mask HTTPS basic authentication credentials (e.g. https://token@github.com -> https://[REDACTED_SECRET]@github.com)
        return text.replaceAll("https://[^@\\s]+@", "https://[REDACTED_SECRET]@");
    }

    private void sendStatusUpdate(String jobId, BuildJobStatus status) {
        messagingTemplate.convertAndSend("/topic/builds/" + jobId, Map.of(
            "type", "STATUS",
            "payload", status.name()
        ));
    }

    private void deleteDirectory(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirectory(f);
            }
        }
        file.delete();
    }
}
