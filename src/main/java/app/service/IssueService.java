package app.service;

import app.dto.IssueRequest;
import app.dto.IssueResponse;
import app.model.Issue;
import app.model.IssuePriority;
import app.model.IssueStatus;
import app.model.IssueType;
import app.security.AuthenticationFacade;
import app.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final AuthenticationFacade authenticationFacade;

    public IssueService(IssueRepository issueRepository, AuthenticationFacade authenticationFacade) {
        this.issueRepository = issueRepository;
        this.authenticationFacade = authenticationFacade;
    }

    public IssueResponse createIssue(IssueRequest request) {
        // Validate required fields
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }

        Issue issue = new Issue();
        
        // Set reporter from authentication context if not provided in request
        try {
            Long currentUserId = authenticationFacade.getCurrentUserId();
            issue.setReporterId(currentUserId);
        } catch (SecurityException e) {
            // If no authentication context and no reporterId provided, reject the request
            if (request.getReporterId() == null) {
                throw new SecurityException("User must be authenticated to create an issue");
            }
            issue.setReporterId(request.getReporterId());
        }
        
        // Map request to issue (default values are already set in the request object)
        mapRequestToIssue(request, issue);
        
        // Set description directly as string (HTML content)
        issue.setDescription(request.getDescription());
        
        Issue savedIssue = issueRepository.save(issue);
        return mapToResponse(savedIssue);
    }

    public IssueResponse getIssueById(Long id) {
        return issueRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Issue not found with id: " + id));
    }

    public List<IssueResponse> getAllIssues() {
        return issueRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<IssueResponse> getIssuesByStatus(String status) {
        try {
            IssueStatus issueStatus = IssueStatus.valueOf(status.toUpperCase());
            return issueRepository.findByStatus(issueStatus).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }

    public List<IssueResponse> getIssuesByAssignee(Long assigneeId) {
        return issueRepository.findByAssigneeId(assigneeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<IssueResponse> getIssuesByReporter(Long reporterId) {
        return issueRepository.findByReporterId(reporterId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void mapRequestToIssue(IssueRequest request, Issue issue) {
        issue.setTitle(request.getTitle());
        issue.setPriority(request.getPriority() != null ? request.getPriority() : IssuePriority.MEDIUM);
        issue.setType(request.getType() != null ? request.getType() : IssueType.TASK);
        issue.setStatus(request.getStatus() != null ? request.getStatus() : IssueStatus.OPEN);
        issue.setResolution(request.getResolution());
        issue.setAssigneeId(request.getAssigneeId());
        issue.setTeamId(request.getTeamId());
        
        if (request.getTags() != null) {
            issue.setTags(String.join(",", request.getTags()));
        }
        
        if (request.getAttachments() != null) {
            issue.setAttachments(String.join(",", request.getAttachments()));
        }
    }

    private IssueResponse mapToResponse(Issue issue) {
        IssueResponse response = new IssueResponse();
        response.setId(issue.getId());
        response.setTitle(issue.getTitle());
        
        // Set description directly as string (HTML content)
        if (issue.getDescription() != null) {
            response.setDescription(issue.getDescription());
        }
        
        response.setPriority(issue.getPriority());
        response.setType(issue.getType());
        response.setStatus(issue.getStatus());
        response.setResolution(issue.getResolution());
        response.setCreatedAt(issue.getCreatedAt());
        response.setUpdatedAt(issue.getUpdatedAt());
        
        // Set reporter information
        response.setReporterId(issue.getReporterId());
        
        // If you want to include full reporter details, you can fetch them here
        // and set them using response.setReporter(reporterDetails);
        
        response.setAssigneeId(issue.getAssigneeId());
        response.setTeamId(issue.getTeamId());
        
        if (issue.getTags() != null && !issue.getTags().isEmpty()) {
            response.setTags(List.of(issue.getTags().split(",")));
        }
        
        if (issue.getAttachments() != null && !issue.getAttachments().isEmpty()) {
            response.setAttachments(List.of(issue.getAttachments().split(",")));
        }
        
        return response;
    }
}
