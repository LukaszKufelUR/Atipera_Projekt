package com.atipera.recruitmenttask;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GitHubClient {

    private final RestClient restClient;

    public GitHubClient(RestClient.Builder builder, @Value("${github.api.url:https://api.github.com}") String apiUrl) {
        this.restClient = builder.baseUrl(apiUrl).build();
    }

    public List<RepositoryResponse> getRepositories(String username) {
        return restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<BranchResponse> getBranches(String username, String repoName) {
        return restClient.get()
                .uri("/repos/{username}/{repo}/branches", username, repoName)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}