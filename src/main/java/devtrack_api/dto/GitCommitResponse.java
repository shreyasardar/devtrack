
package devtrack_api.dto;

import java.time.LocalDateTime;

public class GitCommitResponse {

    private Long id;
    private String commitHash;
    private String message;
    private String authorName;
    private String authorEmail;
    private String commitUrl;
    private LocalDateTime committedAt;
    private Long projectId;
    private Long issueId;

    public GitCommitResponse(
            Long id,
            String commitHash,
            String message,
            String authorName,
            String authorEmail,
            String commitUrl,
            LocalDateTime committedAt,
            Long projectId,
            Long issueId) {

        this.id = id;
        this.commitHash = commitHash;
        this.message = message;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.commitUrl = commitUrl;
        this.committedAt = committedAt;
        this.projectId = projectId;
        this.issueId = issueId;
    }

    public Long getId() {
        return id;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public String getCommitUrl() {
        return commitUrl;
    }

    public LocalDateTime getCommittedAt() {
        return committedAt;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getIssueId() {
        return issueId;
    }
}
