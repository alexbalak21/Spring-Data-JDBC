package app.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Issue {
    private Long id;
    private String title;
    private String description;
    private IssuePriority priority;
    private IssueType type;
    private IssueStatus status;
    private String resolution;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long reporterId;
    private Long assigneeId;
    private Long teamId;
    private String tags; // Comma-separated tags
    private String attachments; // Comma-separated URLs or file paths

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public void setPriority(IssuePriority priority) {
        this.priority = priority;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<String> getTagsAsList() {
        return tags == null || tags.isEmpty() ? List.of() : 
               Arrays.asList(tags.split(","));
    }

    public void setTagsFromList(List<String> tags) {
        this.tags = tags == null ? null : String.join(",", tags);
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public List<String> getAttachmentsAsList() {
        return attachments == null || attachments.isEmpty() ? List.of() : 
               Arrays.asList(attachments.split(","));
    }

    public void setAttachmentsFromList(List<String> attachments) {
        this.attachments = attachments == null ? null : String.join(",", attachments);
    }
}
