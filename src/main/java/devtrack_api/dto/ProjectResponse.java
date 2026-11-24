
package devtrack_api.dto;

import java.time.LocalDateTime;

public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    // GitHub repository details
    private String githubOwner;
    private String githubRepo;
    private String githubUrl;
    private String githubDefaultBranch;

    public ProjectResponse() {
    }

    public ProjectResponse(
            Long id,
            String name,
            String description,
            LocalDateTime createdAt,
            String githubOwner,
            String githubRepo,
            String githubUrl,
            String githubDefaultBranch) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.githubOwner = githubOwner;
        this.githubRepo = githubRepo;
        this.githubUrl = githubUrl;
        this.githubDefaultBranch = githubDefaultBranch;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getGithubOwner() {
        return githubOwner;
    }

    public String getGithubRepo() {
        return githubRepo;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getGithubDefaultBranch() {
        return githubDefaultBranch;
    }
}
