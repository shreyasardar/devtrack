
package devtrack_api.repository;

import devtrack_api.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long> {

    // Get all labels belonging to a project
    List<Label> findByProjectId(Long projectId);

    // Check whether a label name already exists in a project
    boolean existsByNameIgnoreCaseAndProjectId(
            String name,
            Long projectId
    );

    // Find a specific label inside a project
    Optional<Label> findByIdAndProjectId(
            Long id,
            Long projectId
    );
}
