package app.repository;

import app.model.Issue;
import app.model.IssuePriority;
import app.model.IssueStatus;
import app.model.IssueType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcIssueRepository implements IssueRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcIssueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Issue> issueRowMapper = (rs, rowNum) -> {
        Issue issue = new Issue();
        issue.setId(rs.getLong("id"));
        issue.setTitle(rs.getString("title"));
        issue.setDescription(rs.getString("description"));
        
        String priorityStr = rs.getString("priority");
        if (priorityStr != null) {
            issue.setPriority(IssuePriority.valueOf(priorityStr.toUpperCase()));
        }
        
        String typeStr = rs.getString("type");
        if (typeStr != null) {
            issue.setType(IssueType.valueOf(typeStr.toUpperCase()));
        }
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            issue.setStatus(IssueStatus.valueOf(statusStr.toUpperCase().replace(" ", "_")));
        }
        
        issue.setResolution(rs.getString("resolution"));
        issue.setCreatedAt(rs.getTimestamp("created_at") != null ? 
                         rs.getTimestamp("created_at").toLocalDateTime() : null);
        issue.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
                         rs.getTimestamp("updated_at").toLocalDateTime() : null);
        issue.setReporterId(rs.getLong("reporter_id"));
        issue.setAssigneeId(rs.getLong("assignee_id"));
        issue.setTeamId(rs.getLong("team_id"));
        issue.setTags(rs.getString("tags"));
        issue.setAttachments(rs.getString("attachments"));
        
        return issue;
    };

    @Override
    public List<Issue> findAll() {
        String sql = "SELECT * FROM issues";
        return jdbcTemplate.query(sql, issueRowMapper);
    }

    @Override
    public Optional<Issue> findById(Long id) {
        String sql = "SELECT * FROM issues WHERE id = ?";
        List<Issue> issues = jdbcTemplate.query(sql, issueRowMapper, id);
        return issues.isEmpty() ? Optional.empty() : Optional.of(issues.get(0));
    }

    @Override
    public List<Issue> findByReporterId(Long reporterId) {
        String sql = "SELECT * FROM issues WHERE reporter_id = ?";
        return jdbcTemplate.query(sql, issueRowMapper, reporterId);
    }

    @Override
    public List<Issue> findByAssigneeId(Long assigneeId) {
        String sql = "SELECT * FROM issues WHERE assignee_id = ?";
        return jdbcTemplate.query(sql, issueRowMapper, assigneeId);
    }

    @Override
    public List<Issue> findByTeamId(Long teamId) {
        String sql = "SELECT * FROM issues WHERE team_id = ?";
        return jdbcTemplate.query(sql, issueRowMapper, teamId);
    }

    @Override
    public List<Issue> findByStatus(String status) {
        String sql = "SELECT * FROM issues WHERE status = ?";
        return jdbcTemplate.query(sql, issueRowMapper, status);
    }

    @Override
    public List<Issue> findByPriority(String priority) {
        String sql = "SELECT * FROM issues WHERE priority = ?";
        return jdbcTemplate.query(sql, issueRowMapper, priority);
    }

    @Override
    public List<Issue> findByType(String type) {
        String sql = "SELECT * FROM issues WHERE type = ?";
        return jdbcTemplate.query(sql, issueRowMapper, type);
    }

    @Override
    public Issue save(Issue issue) {
        if (issue.getId() == null) {
            return create(issue);
        } else {
            update(issue);
            return issue;
        }
    }

    private Issue create(Issue issue) {
        String sql = """
            INSERT INTO issues (title, description, priority, type, status, resolution, 
                              reporter_id, assignee_id, team_id, tags, attachments)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            
        var keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, issue.getTitle());
            ps.setString(2, issue.getDescription());
            ps.setString(3, issue.getPriority() != null ? issue.getPriority().name() : null);
            ps.setString(4, issue.getType() != null ? issue.getType().name() : null);
            ps.setString(5, issue.getStatus() != null ? issue.getStatus().name().toLowerCase().replace("_", " ") : null);
            ps.setString(6, issue.getResolution());
            ps.setObject(7, issue.getReporterId() > 0 ? issue.getReporterId() : null);
            ps.setObject(8, issue.getAssigneeId() > 0 ? issue.getAssigneeId() : null);
            ps.setObject(9, issue.getTeamId() > 0 ? issue.getTeamId() : null);
            ps.setString(10, issue.getTags());
            ps.setString(11, issue.getAttachments());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated key after insert");
        }
        long id = key.longValue();
        return findById(id).orElseThrow(() -> new IllegalStateException("Failed to retrieve saved issue"));
    }

    @Override
    public int update(Issue issue) {
        String sql = """
            UPDATE issues 
            SET title = ?, description = ?, priority = ?, type = ?, status = ?, 
                resolution = ?, reporter_id = ?, assignee_id = ?, team_id = ?, 
                tags = ?, attachments = ?, updated_at = CURRENT_TIMESTAMP 
            WHERE id = ?
            """;
            
        return jdbcTemplate.update(sql,
            issue.getTitle(),
            issue.getDescription(),
            issue.getPriority() != null ? issue.getPriority().name() : null,
            issue.getType() != null ? issue.getType().name() : null,
            issue.getStatus() != null ? issue.getStatus().name().toLowerCase().replace("_", " ") : null,
            issue.getResolution(),
            issue.getReporterId() > 0 ? issue.getReporterId() : null,
            issue.getAssigneeId() > 0 ? issue.getAssigneeId() : null,
            issue.getTeamId() > 0 ? issue.getTeamId() : null,
            issue.getTags(),
            issue.getAttachments(),
            issue.getId()
        );
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM issues WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
