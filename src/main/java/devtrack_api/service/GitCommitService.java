
package devtrack_api.service;

import devtrack_api.dto.GitCommitRequest;
import devtrack_api.dto.GitCommitResponse;
import devtrack_api.entity.GitCommit;
import devtrack_api.entity.Issue;
import devtrack_api.entity.Project;
import devtrack_api.exception.ResourceNotFoundException;
import devtrack_api.repository.GitCommitRepository;
import devtrack_api.repository.IssueRepository;
import devtrack_api.repository.ProjectRepository;

import tools.jackson.databind.JsonNode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GitCommitService {

    private final GitCommitRepository gitCommitRepository;
    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final GitHubApiService gitHubApiService;

    public GitCommitService(
            GitCommitRepository gitCommitRepository,
            IssueRepository issueRepository,
            ProjectRepository projectRepository,
            GitHubApiService gitHubApiService) {

        this.gitCommitRepository = gitCommitRepository;
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.gitHubApiService = gitHubApiService;
    }

    private GitCommitResponse toResponse(GitCommit commit) {

        return new GitCommitResponse(
                commit.getId(),
                commit.getCommitHash(),
                commit.getMessage(),
                commit.getAuthorName(),
                commit.getAuthorEmail(),
                commit.getCommitUrl(),
                commit.getCommittedAt(),
                commit.getProject().getId(),
                commit.getIssue().getId()
        );
    }

    // Link a manually supplied commit to an issue
    @Transactional
    public GitCommitResponse linkCommit(
            Long projectId,
            Long issueId,
            GitCommitRequest request) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + projectId
                        ));

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with id: " + issueId
                        ));

        if (!issue.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException(
                    "Issue does not belong to this project"
            );
        }

        if (project.getGithubOwner() == null
                || project.getGithubRepo() == null) {
            throw new IllegalArgumentException(
                    "Link a GitHub repository to this project first"
            );
        }

        String commitHash = request.getCommitHash().trim();

        if (gitCommitRepository.existsByProjectIdAndCommitHash(
                projectId, commitHash)) {

            throw new IllegalArgumentException(
                    "This commit is already linked to this project"
            );
        }

        GitCommit commit = new GitCommit();

        commit.setCommitHash(commitHash);
        commit.setMessage(request.getMessage().trim());
        commit.setAuthorName(request.getAuthorName());
        commit.setAuthorEmail(request.getAuthorEmail());
        commit.setProject(project);
        commit.setIssue(issue);

        commit.setCommitUrl(
                "https://github.com/"
                        + project.getGithubOwner()
                        + "/"
                        + project.getGithubRepo()
                        + "/commit/"
                        + commitHash
        );

        commit.setCommittedAt(request.getCommittedAt());

        return toResponse(gitCommitRepository.save(commit));
    }

    // Fetch real commits from GitHub and import those referencing an issue
    @Transactional
    public int syncGitHubCommits(Long projectId)
            throws java.io.IOException, InterruptedException {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + projectId
                        ));

        if (project.getGithubOwner() == null
                || project.getGithubRepo() == null) {
            throw new IllegalArgumentException(
                    "Link a GitHub repository to this project first"
            );
        }

        List<JsonNode> githubCommits =
                gitHubApiService.fetchCommits(
                        project.getGithubOwner(),
                        project.getGithubRepo()
                );

        int importedCount = 0;

        Pattern issuePattern = Pattern.compile(
                "(?i)(?:^|\\s)#(\\d+)\\b"
        );

        for (JsonNode githubCommit : githubCommits) {

            String hash = githubCommit.path("sha").asString();

            if (hash.isBlank()
                    || gitCommitRepository.existsByProjectIdAndCommitHash(
                            projectId, hash)) {
                continue;
            }

            JsonNode commitData = githubCommit.path("commit");
            JsonNode author = commitData.path("author");

            String message = commitData.path("message").asString();

            Matcher matcher = issuePattern.matcher(message);

            if (!matcher.find()) {
                continue;
            }

            Long issueId = Long.parseLong(matcher.group(1));

            Issue issue = issueRepository.findById(issueId)
                    .orElse(null);

            // Ignore commits referencing nonexistent or other-project issues
            if (issue == null
                    || !issue.getProject().getId().equals(projectId)) {
                continue;
            }

            GitCommit commit = new GitCommit();

            commit.setCommitHash(hash);
            commit.setMessage(message);
            commit.setAuthorName(
                    author.path("name").asString(null)
            );
            commit.setAuthorEmail(
                    author.path("email").asString(null)
            );

            commit.setProject(project);
            commit.setIssue(issue);

            commit.setCommitUrl(
                    "https://github.com/"
                            + project.getGithubOwner()
                            + "/"
                            + project.getGithubRepo()
                            + "/commit/"
                            + hash
            );

            String date = author.path("date").asString(null);

            if (date != null) {
                commit.setCommittedAt(
                        OffsetDateTime.parse(date).toLocalDateTime()
                );
            }

            gitCommitRepository.save(commit);
            importedCount++;
        }

        return importedCount;
    }

    // Get all commits linked to an issue
    @Transactional(readOnly = true)
    public List<GitCommitResponse> getIssueCommits(
            Long projectId,
            Long issueId) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with id: " + issueId
                        ));

        if (!issue.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException(
                    "Issue does not belong to this project"
            );
        }

        return gitCommitRepository
                .findByIssueIdOrderByCommittedAtDesc(issueId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Get all commits linked to a project
    @Transactional(readOnly = true)
    public List<GitCommitResponse> getProjectCommits(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found with id: " + projectId
            );
        }

        return gitCommitRepository
                .findByProjectIdOrderByCommittedAtDesc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    

}
