
package devtrack_api.service;

import devtrack_api.dto.ProjectRequest;
import devtrack_api.dto.ProjectResponse;
import devtrack_api.entity.Project;
import devtrack_api.exception.ResourceNotFoundException;
import devtrack_api.repository.ProjectRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt()
        );
    }

    public ProjectResponse createProject(ProjectRequest request) {

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());

        return toResponse(projectRepository.save(project));
    }

    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProjectResponse getProjectById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        ));

        return toResponse(project);
    }

    public ProjectResponse updateProject(
            Long id, ProjectRequest request) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        ));

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        return toResponse(projectRepository.save(project));
    }

    public void deleteProject(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        ));

        projectRepository.delete(project);
    }
}
