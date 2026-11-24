
package devtrack_api.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GitHubApiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    public List<JsonNode> fetchCommits(String owner, String repo)
            throws IOException, InterruptedException {

        String url = "https://api.github.com/repos/"
                + owner + "/" + repo + "/commits?per_page=100";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "GitHub API request failed with status: "
                            + response.statusCode()
            );
        }

        JsonNode root = objectMapper.readTree(response.body());

        List<JsonNode> commits = new ArrayList<>();

        for (JsonNode commit : root) {
            commits.add(commit);
        }

        return commits;
    }

    
}
