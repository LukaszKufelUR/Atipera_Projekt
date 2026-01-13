package com.atipera.recruitmenttask;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
class GitHubClient {

    private final RestClient restClient;

    GitHubClient(RestClient.Builder builder, @Value("${github.api.url:https://api.github.com}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    List<GitHubRepo> getRepos(String username) {
        return restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    List<BranchResponse> getBranches(String owner, String repoName) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repoName)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}

record GitHubRepo(String name, Owner owner, boolean fork) {}
record Owner(String login) {}