package com.cypr.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "build_jobs")
public class BuildJob {

    @Id
    private String jobId;

    private String repositoryUrl;
    private String branch;
    private String commitSha;

    @Enumerated(EnumType.STRING)
    private BuildJobStatus status;

    private LocalDateTime triggeredAt;
    private LocalDateTime completedAt;

    public BuildJob() {}

    public BuildJob(String jobId, String repositoryUrl, String branch, String commitSha, BuildJobStatus status, LocalDateTime triggeredAt) {
        this.jobId = jobId;
        this.repositoryUrl = repositoryUrl;
        this.branch = branch;
        this.commitSha = commitSha;
        this.status = status;
        this.triggeredAt = triggeredAt;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public void setCommitSha(String commitSha) {
        this.commitSha = commitSha;
    }

    public BuildJobStatus getStatus() {
        return status;
    }

    public void setStatus(BuildJobStatus status) {
        this.status = status;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(LocalDateTime triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
