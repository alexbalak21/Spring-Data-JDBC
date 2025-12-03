package app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.List;
import java.util.Arrays;

@Entity
@Table(name = "issues")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String title;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description; // TEXT for storing HTML content
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssuePriority priority;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType type;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status;
    @Column(columnDefinition = "TEXT")
    private String resolution;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;
    @Column(name = "assignee_id")
    private Long assigneeId;
    @Column(name = "team_id")
    private Long teamId;
    @Column(columnDefinition = "TEXT")
    private String tags; // Comma-separated tags
    @Column(columnDefinition = "TEXT")
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
