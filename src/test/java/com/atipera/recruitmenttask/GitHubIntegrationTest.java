package com.atipera.recruitmenttask;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8081)
class GitHubIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("github.api.url", () -> "http://localhost:8081");
    }

    @Test
    void shouldFetchDataInParallelAndReturnWithinTimeLimit() {
        String username = "testuser";

        stubFor(get(urlPathEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1000)
                        .withBody("""
                                [
                                    {"name": "repo1", "owner": {"login": "testuser"}, "fork": false},
                                    {"name": "repo2", "owner": {"login": "testuser"}, "fork": false},
                                    {"name": "fork-repo", "owner": {"login": "testuser"}, "fork": true}
                                ]
                                """)));

        stubFor(get(urlPathEqualTo("/repos/" + username + "/repo1/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1000)
                        .withBody("""
                                [{"name": "main", "commit": {"sha": "sha1"}}]
                                """)));

        stubFor(get(urlPathEqualTo("/repos/" + username + "/repo2/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1000)
                        .withBody("""
                                [{"name": "dev", "commit": {"sha": "sha2"}}]
                                """)));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/github/users/" + username + "/repos", String.class);

        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTime(TimeUnit.MILLISECONDS);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("repo1");
        assertThat(response.getBody()).contains("repo2");
        assertThat(response.getBody()).doesNotContain("fork-repo");

        verify(3, getRequestedFor(urlMatching(".*")));

        assertThat(totalTimeMillis).isGreaterThanOrEqualTo(2000);
        assertThat(totalTimeMillis).isLessThan(3000);
    }

    @Test
    void shouldReturn404WhenUserNotFound() {
        String username = "non-existing";
        stubFor(get(urlPathEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse().withStatus(404)));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/github/users/" + username + "/repos", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}