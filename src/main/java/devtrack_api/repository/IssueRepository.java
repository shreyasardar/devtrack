
package devtrack_api.repository;

import java.util.List;
import devtrack_api.entity.Issue;
import devtrack_api.enums.IssueStatus;
import devtrack_api.enums.IssuePriority;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    Page<Issue> findByProjectId(Long projectId, Pageable pageable);

    Page<Issue> findByProjectIdAndStatus(
            Long projectId,
            IssueStatus status,
            Pageable pageable);

    Page<Issue> findByProjectIdAndPriority(
            Long projectId,
            IssuePriority priority,
            Pageable pageable);

    Page<Issue> findByProjectIdAndStatusAndPriority(
            Long projectId,
            IssueStatus status,
            IssuePriority priority,
            Pageable pageable);
            List<Issue> findDistinctByProjectIdAndLabelsId(
    Long projectId,
    Long labelId
);

     
@Query("""
    SELECT i FROM Issue i
    WHERE i.project.id = :projectId
    AND (:search IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')))
    AND (:status IS NULL OR i.status = :status)
    AND (:priority IS NULL OR i.priority = :priority)
    """)
Page<Issue> searchAndFilterIssues(
        @Param("projectId") Long projectId,
        @Param("search") String search,
        @Param("status") IssueStatus status,
        @Param("priority") IssuePriority priority,
        Pageable pageable
);

}
