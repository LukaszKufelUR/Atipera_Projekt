package com.atipera.recruitmenttask;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
class GitHubService {

    private final GitHubClient gitHubClient;

    GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    List<RepositoryResponse> getUserRepositories(String username) {
        try {
            List<GitHubRepo> allRepos = gitHubClient.getRepos(username);

            return allRepos.stream()
                    .filter(repo -> !repo.fork())
                    .map(repo -> {
                        List<BranchResponse> branches = gitHubClient.getBranches(repo.owner().login(), repo.name());
                        return new RepositoryResponse(repo.name(), repo.owner().login(), branches);
                    })
                    .toList();
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException(e.getMessage());
        }
    }
}