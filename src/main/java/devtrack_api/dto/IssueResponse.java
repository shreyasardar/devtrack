
package devtrack_api.dto;

import devtrack_api.enums.IssueStatus;
import devtrack_api.enums.IssuePriority;

import java.time.LocalDateTime;
import java.util.List;

public class IssueResponse {

    private Long id;
    private String title;
    private String description;
    private IssueStatus status;
    private IssuePriority priority;
    private Long projectId;
    private Long assigneeId;
    private String assigneeName;
    private LocalDateTime createdAt;
    private List<LabelResponse> labels;

    public IssueResponse(
            Long id,
            String title,
            String description,
            IssueStatus status,
            IssuePriority priority,
            Long projectId,
            Long assigneeId,
            String assigneeName,
            LocalDateTime createdAt,
            List<LabelResponse> labels) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.assigneeName = assigneeName;
        this.createdAt = createdAt;
        this.labels = labels;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<LabelResponse> getLabels() {
        return labels;
    }
}
