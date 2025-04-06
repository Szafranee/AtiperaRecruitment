package com.atipera.rekrutacja.controller;

import com.atipera.rekrutacja.dto.Branch;
import com.atipera.rekrutacja.dto.RepositoryResponse;
import com.atipera.rekrutacja.exception.GlobalExceptionHandler;
import com.atipera.rekrutacja.service.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for GithubController using MockMvc and Mockito.
 * We use MockitoExtension to manage mocks and MockMvc in standalone mode to test the controller.
 */
@ExtendWith(MockitoExtension.class)
public class GithubControllerIntegrationTest {

    private MockMvc mockMvc;

    // Mock the GithubService to control its behavior in tests
    @Mock
    private GithubService githubService;

    // Inject the mock GithubService into GithubController
    @InjectMocks
    private GithubController githubController;

    @BeforeEach
    void setUp() {
        // Set up MockMvc in standalone mode with the controller and exception handler
        mockMvc = MockMvcBuilders.standaloneSetup(githubController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /**
     * Test case: Fetch repositories for an existing user.
     * Expected: Returns 200 OK with a list of repositories in JSON format.
     */
    @Test
    void shouldReturnRepositoriesForExistingUser() throws Exception {
        // Arrange: Create test data for repositories
        Branch branch1 = new Branch("main", "sha1");
        Branch branch2 = new Branch("dev", "sha2");
        RepositoryResponse repo1 = new RepositoryResponse("repo1", "Szafranee", Arrays.asList(branch1));
        RepositoryResponse repo2 = new RepositoryResponse("repo2", "Szafranee", Arrays.asList(branch2));
        List<RepositoryResponse> repositories = Arrays.asList(repo1, repo2);

        // Mock the GithubService to return the list of repositories for user "Szafranee"
        when(githubService.getUserRepositories("Szafranee")).thenReturn(repositories);

        // Act & Assert: Perform the GET request and verify the response
        mockMvc.perform(get("/api/users/Szafranee/repos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    [
                        {
                            "name": "repo1",
                            "ownerLogin": "Szafranee",
                            "branches": [
                                {"name": "main", "lastCommitSha": "sha1"}
                            ]
                        },
                        {
                            "name": "repo2",
                            "ownerLogin": "Szafranee",
                            "branches": [
                                {"name": "dev", "lastCommitSha": "sha2"}
                            ]
                        }
                    ]
                """));
    }

    /**
     * Test case: Fetch repositories for a non-existing user.
     * Expected: Returns 404 Not Found with an error response when the user does not exist.
     */
    @Test
    void shouldReturn404ForNonExistingUser() throws Exception {
        // Arrange: Mock the GithubService to throw an HttpClientErrorException for a non-existing user
        when(githubService.getUserRepositories("nonExistingUser"))
                .thenThrow(new HttpClientErrorException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Failed to fetch repositories for user nonExistingUser"
                ));

        // Act & Assert: Perform the GET request and verify the response
        mockMvc.perform(get("/api/users/nonExistingUser/repos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
            {
                "status": 404,
                "message": "404 Failed to fetch repositories for user nonExistingUser"
            }
        """));
    }

    /**
     * Test case: Fetch repositories for a user with no repositories.
     * Expected: Returns 200 OK with an empty list.
     */
    @Test
    void shouldReturnEmptyListForUserWithNoRepositories() throws Exception {
        // Arrange: Mock the GithubService to return an empty list for a user with no repositories
        when(githubService.getUserRepositories("userWithNoRepos")).thenReturn(Collections.emptyList());

        // Act & Assert: Perform the GET request and verify the response
        mockMvc.perform(get("/api/users/userWithNoRepos/repos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    /**
     * Test case: Fetch repositories for a user whose repositories are all forks.
     * Expected: Returns 200 OK with an empty list (forks are filtered out by GithubService).
     */
    @Test
    void shouldReturnEmptyListForUserWithOnlyForks() throws Exception {
        // Arrange: GithubService filters out forks, so even if the API returns forks,
        // the service will return an empty list. We mock it directly as an empty list.
        when(githubService.getUserRepositories("userWithForks")).thenReturn(Collections.emptyList());

        // Act & Assert: Perform the GET request and verify the response
        mockMvc.perform(get("/api/users/userWithForks/repos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    /**
     * Test case: Fetch repositories for a user with a repository that has no branches.
     * Expected: Returns 200 OK with a repository that has an empty branches list.
     */
    @Test
    void shouldHandleRepositoryWithNoBranches() throws Exception {
        // Arrange: Create a repository with no branches
        RepositoryResponse repo = new RepositoryResponse("repoNoBranches", "userWithNoBranches", Collections.emptyList());
        List<RepositoryResponse> repositories = Arrays.asList(repo);

        // Mock the GithubService to return a repository with no branches
        when(githubService.getUserRepositories("userWithNoBranches")).thenReturn(repositories);

        // Act & Assert: Perform the GET request and verify the response
        mockMvc.perform(get("/api/users/userWithNoBranches/repos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    [
                        {
                            "name": "repoNoBranches",
                            "ownerLogin": "userWithNoBranches",
                            "branches": []
                        }
                    ]
                """));
    }
}