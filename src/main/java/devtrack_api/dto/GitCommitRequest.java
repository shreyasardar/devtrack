
package devtrack_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class GitCommitRequest {

    @NotBlank(message = "Commit hash is required")
    @Size(max = 64)
    private String commitHash;

    @NotBlank(message = "Commit message is required")
    @Size(max = 1000)
    private String message;

    @Size(max = 150)
    private String authorName;

    @Size(max = 150)
    private String authorEmail;

    private LocalDateTime committedAt;

    public GitCommitRequest() {
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public LocalDateTime getCommittedAt() {
        return committedAt;
    }

    public void setCommittedAt(LocalDateTime committedAt) {
        this.committedAt = committedAt;
    }
}
