
package devtrack_api.repository;

import devtrack_api.entity.GitCommit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GitCommitRepository extends JpaRepository<GitCommit, Long> {

    // Get all commits linked to an issue
    List<GitCommit> findByIssueIdOrderByCommittedAtDesc(Long issueId);

    // Get all commits belonging to a project
    List<GitCommit> findByProjectIdOrderByCommittedAtDesc(Long projectId);

    // Prevent duplicate commits within the same project
    Optional<GitCommit> findByProjectIdAndCommitHash(
            Long projectId,
            String commitHash
    );

    // Check whether a commit already exists
    boolean existsByProjectIdAndCommitHash(
            Long projectId,
            String commitHash
    );
}
