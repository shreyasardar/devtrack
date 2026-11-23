
package devtrack_api.controller;

import devtrack_api.dto.LabelRequest;
import devtrack_api.dto.LabelResponse;
import devtrack_api.service.LabelService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    // CREATE LABEL
    @PostMapping
    public ResponseEntity<LabelResponse> createLabel(
            @PathVariable Long projectId,
            @Valid @RequestBody LabelRequest request) {

        LabelResponse response = labelService.createLabel(
                projectId,
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // GET ALL LABELS FOR A PROJECT
    @GetMapping
    public ResponseEntity<List<LabelResponse>> getProjectLabels(
            @PathVariable Long projectId) {

        return ResponseEntity.ok(
                labelService.getProjectLabels(projectId)
        );
    }

    // DELETE LABEL
    @DeleteMapping("/{labelId}")
    public ResponseEntity<Void> deleteLabel(
            @PathVariable Long projectId,
            @PathVariable Long labelId) {

        labelService.deleteLabel(projectId, labelId);

        return ResponseEntity.noContent().build();
    }
}
