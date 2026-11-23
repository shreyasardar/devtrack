
package devtrack_api.controller;

import java.util.List;

import devtrack_api.dto.IssueRequest;
import devtrack_api.dto.IssueResponse;
import devtrack_api.dto.StatusUpdateRequest;
import devtrack_api.dto.AssignIssueRequest;
import devtrack_api.enums.IssuePriority;
import devtrack_api.enums.IssueStatus;
import devtrack_api.service.IssueService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/projects/{projectId}/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    // CREATE ISSUE
    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(
            @PathVariable Long projectId,
            @Valid @RequestBody IssueRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(issueService.createIssue(projectId, request));
    }

    // GET SINGLE ISSUE BY ID
    @GetMapping("/{issueId}")
    public ResponseEntity<IssueResponse> getIssueById(
            @PathVariable Long projectId,
            @PathVariable Long issueId) {

        return ResponseEntity.ok(
                issueService.getIssueById(projectId, issueId)
        );
    }

    // GET ISSUES WITH FILTERING, PAGINATION AND SORTING
    @GetMapping
    public ResponseEntity<Page<IssueResponse>> getProjectIssues(
            @PathVariable Long projectId,
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) IssuePriority priority,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Page<IssueResponse> issues = issueService.getProjectIssues(
                projectId,
                status,
                priority,
                search,
                page,
                size,
                sortBy,
                direction
        );

        return ResponseEntity.ok(issues);
    }

    // UPDATE ISSUE STATUS
    @PatchMapping("/{issueId}/status")
    public ResponseEntity<IssueResponse> updateIssueStatus(
            @PathVariable Long projectId,
            @PathVariable Long issueId,
            @Valid @RequestBody StatusUpdateRequest request) {

        return ResponseEntity.ok(
                issueService.updateIssueStatus(
                        projectId,
                        issueId,
                        request.getStatus()
                )
        );
    }

    // UPDATE ISSUE DETAILS
    @PutMapping("/{issueId}")
    public ResponseEntity<IssueResponse> updateIssue(
            @PathVariable Long projectId,
            @PathVariable Long issueId,
            @Valid @RequestBody IssueRequest request) {

        return ResponseEntity.ok(
                issueService.updateIssue(projectId, issueId, request)
        );
    }

    // DELETE ISSUE
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{issueId}")
    public ResponseEntity<Void> deleteIssue(
            @PathVariable Long projectId,
            @PathVariable Long issueId) {

        issueService.deleteIssue(projectId, issueId);

        return ResponseEntity.noContent().build();
    }

    // ASSIGN ISSUE TO DEVELOPER
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/{issueId}/assign")
    public ResponseEntity<IssueResponse> assignIssue(
            @PathVariable Long projectId,
            @PathVariable Long issueId,
            @Valid @RequestBody AssignIssueRequest request) {

        IssueResponse response = issueService.assignIssue(
                projectId,
                issueId,
                request.getUserId()
        );

        return ResponseEntity.ok(response);
    }

// ASSIGN LABEL TO ISSUE
@PostMapping("/{issueId}/labels/{labelId}")
public ResponseEntity<IssueResponse> assignLabel(
        @PathVariable Long projectId,
        @PathVariable Long issueId,
        @PathVariable Long labelId) {

    return ResponseEntity.ok(
            issueService.assignLabel(projectId, issueId, labelId)
    );
}

// REMOVE LABEL FROM ISSUE
@DeleteMapping("/{issueId}/labels/{labelId}")
public ResponseEntity<IssueResponse> removeLabel(
        @PathVariable Long projectId,
        @PathVariable Long issueId,
        @PathVariable Long labelId) {

    return ResponseEntity.ok(
            issueService.removeLabel(projectId, issueId, labelId)
    );
}
@GetMapping("/by-label/{labelId}")
public ResponseEntity<List<IssueResponse>> getIssuesByLabel(
        @PathVariable Long projectId,
        @PathVariable Long labelId
) {
    return ResponseEntity.ok(
            issueService.getIssuesByLabel(projectId, labelId)
    );
}

}
