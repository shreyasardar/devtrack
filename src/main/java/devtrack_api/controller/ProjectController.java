
package devtrack_api.controller;

import devtrack_api.dto.ProjectRequest;
import devtrack_api.dto.ProjectResponse;
import devtrack_api.service.ProjectService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // ADMIN and MANAGER can create projects
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(request));
    }

    // All authenticated users can view projects
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {

        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id) {

        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    // ADMIN and MANAGER can update projects
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {

        return ResponseEntity.ok(
                projectService.updateProject(id, request)
        );
    }

    // Only ADMIN can delete projects
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id) {

        projectService.deleteProject(id);

        return ResponseEntity.noContent().build();
    }
}
