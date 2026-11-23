
package devtrack_api.entity;

import devtrack_api.enums.IssueStatus;
import devtrack_api.enums.IssuePriority;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status = IssueStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssuePriority priority = IssuePriority.MEDIUM;

    // Project to which this issue belongs
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // User assigned to this issue
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    private LocalDateTime createdAt;

    // Multiple labels can be assigned to an issue
    @ManyToMany
    @JoinTable(
        name = "issue_labels",
        joinColumns = @JoinColumn(name = "issue_id"),
        inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();

    public Issue() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Status
    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    // Priority
    public IssuePriority getPriority() {
        return priority;
    }

    public void setPriority(IssuePriority priority) {
        this.priority = priority;
    }

    // Project
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    // Assignee
    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    // Created date
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Labels
    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }
}
