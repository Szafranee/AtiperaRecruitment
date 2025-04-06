package com.atipera.rekrutacja.dto;

import java.util.List;

/**
 * Record representing a GitHub repository response.
 * Contains basic repository information along with its branches.
 *
 * @param name The name of the repository
 * @param ownerLogin The GitHub username of the repository owner
 * @param branches List of branches in the repository
 */
public record RepositoryResponse(String name, String ownerLogin, List<Branch> branches) {
}
