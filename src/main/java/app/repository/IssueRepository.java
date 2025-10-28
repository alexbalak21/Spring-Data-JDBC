package app.repository;

import app.model.Issue;
import java.util.List;
import java.util.Optional;

public interface IssueRepository {
    List<Issue> findAll();
    Optional<Issue> findById(Long id);
    List<Issue> findByReporterId(Long reporterId);
    List<Issue> findByAssigneeId(Long assigneeId);
    List<Issue> findByTeamId(Long teamId);
    List<Issue> findByStatus(String status);
    List<Issue> findByPriority(String priority);
    List<Issue> findByType(String type);
    Issue save(Issue issue);
    int update(Issue issue);
    int deleteById(Long id);
}
