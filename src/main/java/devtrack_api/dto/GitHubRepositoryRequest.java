
package devtrack_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GitHubRepositoryRequest {

    @NotBlank(message = "GitHub owner is required")
    @Size(max = 100)
    private String githubOwner;

    @NotBlank(message = "Repository name is required")
    @Size(max = 100)
    private String githubRepo;

    @NotBlank(message = "Default branch is required")
    @Size(max = 100)
    private String githubDefaultBranch;

    public GitHubRepositoryRequest() {
    }

    public String getGithubOwner() {
        return githubOwner;
    }

    public void setGithubOwner(String githubOwner) {
        this.githubOwner = githubOwner;
    }

    public String getGithubRepo() {
        return githubRepo;
    }

    public void setGithubRepo(String githubRepo) {
        this.githubRepo = githubRepo;
    }

    public String getGithubDefaultBranch() {
        return githubDefaultBranch;
    }

    public void setGithubDefaultBranch(String githubDefaultBranch) {
        this.githubDefaultBranch = githubDefaultBranch;
    }
}
