
package devtrack_api.controller;

import devtrack_api.dto.GitCommitRequest;
import devtrack_api.dto.GitCommitResponse;
import devtrack_api.service.GitCommitService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}")
public class GitCommitController {

    private final GitCommitService gitCommitService;

    public GitCommitController(GitCommitService gitCommitService) {
        this.gitCommitService = gitCommitService;
    }

    // Link a GitHub commit to an issue
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DEVELOPER')")
    @PostMapping("/issues/{issueId}/commits")
    public ResponseEntity<GitCommitResponse> linkCommit(
            @PathVariable Long projectId,
            @PathVariable Long issueId,
            @Valid @RequestBody GitCommitRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gitCommitService.linkCommit(
                        projectId, issueId, request
                ));
    }

    // Get all commits linked to an issue
    @GetMapping("/issues/{issueId}/commits")
    public ResponseEntity<List<GitCommitResponse>> getIssueCommits(
            @PathVariable Long projectId,
            @PathVariable Long issueId) {

        return ResponseEntity.ok(
                gitCommitService.getIssueCommits(projectId, issueId)
        );
    }

    // Get all commits linked to a project
    @GetMapping("/commits")
    public ResponseEntity<List<GitCommitResponse>> getProjectCommits(
            @PathVariable Long projectId) {

        return ResponseEntity.ok(
                gitCommitService.getProjectCommits(projectId)
        );
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@PostMapping("/sync")
public ResponseEntity<?> syncGitHubCommits(
        @PathVariable Long projectId) throws Exception {

    int importedCount =
            gitCommitService.syncGitHubCommits(projectId);

    return ResponseEntity.ok(
            java.util.Map.of(
                    "message", "GitHub commits synchronized successfully",
                    "importedCount", importedCount
            )
    );
}
}
