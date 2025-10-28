package app.controller;

import app.dto.IssueRequest;
import app.dto.IssueResponse;
import app.service.IssueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(@RequestBody IssueRequest issueRequest) {
        IssueResponse response = issueService.createIssue(issueRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable Long id) {
        IssueResponse response = issueService.getIssueById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<IssueResponse>> getAllIssues() {
        List<IssueResponse> issues = issueService.getAllIssues();
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<IssueResponse>> getIssuesByStatus(@PathVariable String status) {
        List<IssueResponse> issues = issueService.getIssuesByStatus(status);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<List<IssueResponse>> getIssuesByAssignee(@PathVariable Long assigneeId) {
        List<IssueResponse> issues = issueService.getIssuesByAssignee(assigneeId);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/reporter/{reporterId}")
    public ResponseEntity<List<IssueResponse>> getIssuesByReporter(@PathVariable Long reporterId) {
        List<IssueResponse> issues = issueService.getIssuesByReporter(reporterId);
        return ResponseEntity.ok(issues);
    }
}
