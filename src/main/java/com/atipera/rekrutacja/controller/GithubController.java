package com.atipera.rekrutacja.controller;

import com.atipera.rekrutacja.dto.RepositoryResponse;
import com.atipera.rekrutacja.service.GithubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<List<RepositoryResponse>> getUserRepositories (@PathVariable String username) {
        List<RepositoryResponse> repositories = githubService.getUserRepositories(username);
        return ResponseEntity.ok(repositories);
    }
}
