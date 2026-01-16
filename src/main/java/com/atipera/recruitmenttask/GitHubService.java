package com.atipera.recruitmenttask;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
class GitHubService {

    private final GitHubClient gitHubClient;

    GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    List<RepositoryResponse> getUserRepositories(String username) {
        List<RepositoryResponse> repositories = gitHubClient.getRepositories(username);

        return repositories.stream()
                .filter(repo -> !repo.isFork()) // Zmiana z .fork() na .isFork()
                .parallel()
                .peek(repo -> {
                    List<BranchResponse> branches = gitHubClient.getBranches(username, repo.getName()); // Zmiana z .name() na .getName()
                    repo.setBranches(branches);
                })
                .toList();
    }
}