package com.atipera.recruitmenttask;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github")
class GitHubController {

    private final GitHubService gitHubService;

    GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/users/{username}/repos")
    ResponseEntity<List<RepositoryResponse>> getUserRepositories(@PathVariable String username) {
        List<RepositoryResponse> repositories = gitHubService.getUserRepositories(username);
        return ResponseEntity.ok(repositories);
    }
}