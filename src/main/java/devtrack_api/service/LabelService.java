
package devtrack_api.service;

import devtrack_api.dto.LabelRequest;
import devtrack_api.dto.LabelResponse;
import devtrack_api.entity.Label;
import devtrack_api.entity.Project;
import devtrack_api.exception.ResourceNotFoundException;
import devtrack_api.repository.LabelRepository;
import devtrack_api.repository.ProjectRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LabelService {

    private final LabelRepository labelRepository;
    private final ProjectRepository projectRepository;

    public LabelService(
            LabelRepository labelRepository,
            ProjectRepository projectRepository) {

        this.labelRepository = labelRepository;
        this.projectRepository = projectRepository;
    }

    // CONVERT ENTITY TO RESPONSE DTO
    private LabelResponse toResponse(Label label) {

        return new LabelResponse(
                label.getId(),
                label.getName(),
                label.getDescription(),
                label.getColor(),
                label.getProject().getId()
        );
    }

    // CREATE LABEL
    @Transactional
    public LabelResponse createLabel(
            Long projectId,
            LabelRequest request) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + projectId));

        String labelName = request.getName().trim();

        if (labelRepository.existsByNameIgnoreCaseAndProjectId(
                labelName, projectId)) {

            throw new IllegalArgumentException(
                    "Label already exists in this project: " + labelName);
        }

        Label label = new Label();
        label.setName(labelName);
        label.setDescription(request.getDescription());
        label.setColor(request.getColor());
        label.setProject(project);

        return toResponse(labelRepository.save(label));
    }

    // GET ALL LABELS FOR A PROJECT
    @Transactional(readOnly = true)
    public List<LabelResponse> getProjectLabels(Long projectId) {

        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + projectId));

        return labelRepository.findByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // DELETE LABEL
    @Transactional
    public void deleteLabel(Long projectId, Long labelId) {

        Label label = labelRepository.findByIdAndProjectId(
                labelId, projectId
        ).orElseThrow(() -> new ResourceNotFoundException(
                "Label not found in project: " + projectId));

        labelRepository.delete(label);
    }
}
