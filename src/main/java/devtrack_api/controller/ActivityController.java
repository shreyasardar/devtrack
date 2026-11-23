package devtrack_api.controller;

import devtrack_api.dto.ActivityResponse;
import devtrack_api.service.ActivityService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/issues/{issueId}/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getIssueActivities(
            @PathVariable Long projectId,
            @PathVariable Long issueId
    ) {
        return ResponseEntity.ok(
                activityService.getIssueActivities(issueId)
        );
    }
}