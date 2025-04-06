package com.atipera.rekrutacja.dto;

import java.util.List;

public record RepositoryResponse(String name, String ownerLogin, List<Branch> branches) {
}