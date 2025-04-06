package com.atipera.rekrutacja.service;

import com.atipera.rekrutacja.dto.Branch;
import com.atipera.rekrutacja.dto.RepositoryResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GithubService {
    private final RestTemplate restTemplate;
    private final String githubApiUrl;
    private final ObjectMapper objectMapper;

    public GithubService(RestTemplate restTemplate, @Value("${github.api.url}") String githubApiUrl) {
        this.restTemplate = restTemplate;
        this.githubApiUrl = githubApiUrl;
        this.objectMapper = new ObjectMapper();
    }

    public List<RepositoryResponse> getUserRepositories(String username) throws HttpClientErrorException {
        String reposUrl = githubApiUrl + "/users/" + username + "/repos";
        String reposJson;
        try {
            reposJson = restTemplate.getForObject(reposUrl, String.class);
            if (reposJson == null) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "No repositories found for user: " + username);
            }
        } catch (HttpClientErrorException e) {
            throw new HttpClientErrorException(e.getStatusCode(), "Failed to fetch repositories for user " + username);
        }

        List<JsonNode> nonForkRepos = filterNonForkRepositories(reposJson);
        List<RepositoryResponse> repositories = new ArrayList<>();
        for (JsonNode repo : nonForkRepos) {
            String repoName = repo.get("name").asText();
            String ownerLogin = repo.get("owner").get("login").asText();

            List<Branch> branches = fetchBranches(username, repoName);

            repositories.add(new RepositoryResponse(repoName, ownerLogin, branches));
        }

        return repositories;
    }

    private List<JsonNode> filterNonForkRepositories(String reposJson) {
        try {
            JsonNode reposArray = objectMapper.readTree(reposJson);

            List<JsonNode> nonForkRepos = new ArrayList<>();
            for (JsonNode repo : reposArray) {
                if (!repo.get("fork").asBoolean()) {
                    nonForkRepos.add(repo);
                }
            }

            return nonForkRepos;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<Branch> fetchBranches(String username, String repoName) {
        String branchesUrl = githubApiUrl + "/repos/" + username + "/" + repoName + "/branches";
        String branchesJson;
        try {
            branchesJson = restTemplate.getForObject(branchesUrl, String.class);
            if (branchesJson == null) {
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            return Collections.emptyList();
        }

        try {
            JsonNode branchesArray = objectMapper.readTree(branchesJson);

            List<Branch> branches = new ArrayList<>();
            for (JsonNode branch : branchesArray) {
                String branchName = branch.get("name").asText();
                String lastCommitSha = branch.get("commit").get("sha").asText();

                branches.add(new Branch(branchName, lastCommitSha));
            }

            return branches;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}