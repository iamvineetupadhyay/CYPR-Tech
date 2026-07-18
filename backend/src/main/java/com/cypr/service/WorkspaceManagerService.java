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

    @Autowired
    public WorkspaceManagerService(BuildJobRepository buildJobRepository, SimpMessagingTemplate messagingTemplate, EmailService emailService) {
        this.buildJobRepository = buildJobRepository;
        this.messagingTemplate = messagingTemplate;
        this.emailService = emailService;
        this.executorService = Executors.newCachedThreadPool();
    }

    public BuildJob triggerBuild(String repositoryUrl, String branch, String commitSha) {
        String jobId = UUID.randomUUID().toString();
        BuildJob job = new BuildJob(jobId, repositoryUrl, branch, commitSha, BuildJobStatus.PENDING, LocalDateTime.now());
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
                List<String> checkoutCmd = List.of("git", "checkout", job.getCommitSha());
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

        // Send automated notifications to administrators on failure
        try {
            emailService.sendBuildFailedAlert("vineetk5704@gmail.com", job.getJobId(), job.getRepositoryUrl(), job.getBranch(), reason);
            emailService.sendBuildFailedAlert("admin@cypr.com", job.getJobId(), job.getRepositoryUrl(), job.getBranch(), reason);
            sendLogLine(job.getJobId(), "[CYPR BUILD ENGINE] Notification email alerts successfully dispatched.");
        } catch (Exception e) {
            sendLogLine(job.getJobId(), "[CYPR BUILD ENGINE] WARNING: Failed to dispatch alert emails: " + e.getMessage());
        }
    }

    public boolean executeNativeCommand(String jobId, List<String> command, File workingDirectory) {
        sendLogLine(jobId, "[CYPR BUILD ENGINE] Running command: " + String.join(" ", command));
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
        messagingTemplate.convertAndSend("/topic/builds/" + jobId, Map.of(
            "type", "LOG",
            "payload", logLine
        ));
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
