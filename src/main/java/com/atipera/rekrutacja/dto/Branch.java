package com.atipera.rekrutacja.dto;

/**
 * Record representing a GitHub repository branch.
 * Contains information about the branch name and its last commit SHA.
 *
 * @param name The name of the branch
 * @param lastCommitSha The SHA hash of the last commit on this branch
 */
public record Branch(String name, String lastCommitSha) {
}
