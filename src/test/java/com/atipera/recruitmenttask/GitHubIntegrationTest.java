package com.atipera.recruitmenttask;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8081)
class GitHubIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void configurePropertiżes(DynamicPropertyRegistry registry) {
        registry.add("github.api.url", () -> "http://localhost:8081");
    }

    @Test
    void shouldReturnRepositoriesWithoutForks() {
        String username = "testuser";

        stubFor(get(urlPathEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {"name": "repo1", "owner": {"login": "testuser"}, "fork": false},
                                    {"name": "fork-repo", "owner": {"login": "testuser"}, "fork": true}
                                ]
                                """)));

        stubFor(get(urlPathEqualTo("/repos/" + username + "/repo1/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {"name": "main", "commit": {"sha": "123sha"}}
                                ]
                                """)));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/github/users/" + username + "/repos", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("repo1");
        assertThat(response.getBody()).contains("123sha");
        assertThat(response.getBody()).doesNotContain("fork-repo");
    }

    @Test
    void shouldReturn404WhenUserNotFound() {
        String username = "non-existing";

        stubFor(get(urlPathEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse().withStatus(404)));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/github/users/" + username + "/repos", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("\"status\":404");
    }
}