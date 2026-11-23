
package devtrack_api.controller;

import devtrack_api.dto.CommentRequest;
import devtrack_api.dto.CommentResponse;
import devtrack_api.service.CommentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/issues/{issueId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // CREATE COMMENT
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long projectId,
            @PathVariable Long issueId,
            @Valid @RequestBody CommentRequest request) {

        CommentResponse response = commentService.createComment(
                projectId,
                issueId,
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // GET ALL COMMENTS FOR AN ISSUE
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getIssueComments(
            @PathVariable Long projectId,
            @PathVariable Long issueId) {

        return ResponseEntity.ok(
                commentService.getIssueComments(projectId, issueId)
        );
    }

    // UPDATE COMMENT
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long projectId,
            @PathVariable Long issueId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request) {

        return ResponseEntity.ok(
                commentService.updateComment(
                        projectId,
                        issueId,
                        commentId,
                        request
                )
        );
    }

    // DELETE COMMENT
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long projectId,
            @PathVariable Long issueId,
            @PathVariable Long commentId) {

        commentService.deleteComment(
                projectId,
                issueId,
                commentId
        );

        return ResponseEntity.noContent().build();
    }
}
