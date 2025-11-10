package app.repository;

import app.model.Issue;
import app.model.IssuePriority;
import app.model.IssueStatus;
import app.model.IssueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByReporterId(Long reporterId);
    List<Issue> findByAssigneeId(Long assigneeId);
    List<Issue> findByTeamId(Long teamId);
    List<Issue> findByStatus(IssueStatus status);
    List<Issue> findByPriority(IssuePriority priority);
    List<Issue> findByType(IssueType type);
    
    // Custom query for complex updates if needed
    @Modifying
    @Query("UPDATE Issue i SET i.status = :status, i.updatedAt = CURRENT_TIMESTAMP WHERE i.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") IssueStatus status);
    
    @Modifying
    @Query("UPDATE Issue i SET i.assigneeId = :assigneeId, i.updatedAt = CURRENT_TIMESTAMP WHERE i.id = :id")
    int updateAssignee(@Param("id") Long id, @Param("assigneeId") Long assigneeId);
}
