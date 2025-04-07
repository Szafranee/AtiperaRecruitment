# GitHub Repository Browser API

## Overview
This Spring Boot application provides a REST API for retrieving information about GitHub repositories. It allows users to fetch non-fork repositories and their branch information for any GitHub username.

## Features
- Fetch non-fork repositories for a given GitHub username
- Retrieve branch information for each repository
- Error handling for non-existent users and other API issues
- JSON response format

## Technical Stack
- Java 21
- Spring Boot
- Spring Web
- Jackson for JSON processing
- Maven for dependency management

## Prerequisites
- JDK 21 or higher (required by the project configuration)
- Maven 3.6.3 or higher (required for Spring Boot 3.x)
- Internet connection (for GitHub API access)
- Spring Boot 3.4.4 compatibility

## Setup and Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/AtiperaRecruitment.git
cd AtiperaRecruitment
```

2. Configure GitHub API URL in `application.properties`:
```properties
github.api.url=https://api.github.com
```

3. Build the project:
```bash
./mvnw clean install
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Documentation

### Get User Repositories
Retrieves all non-fork repositories for a specified GitHub user.

#### Endpoint
```
GET /api/users/{username}/repositories
```

#### Parameters
- `username` (path parameter) - GitHub username

#### Successful Response (200 OK)
```json
[
  {
    "name": "repository-name",
    "ownerLogin": "username",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "commitHash123"
      }
    ]
  }
]
```

#### Error Responses

1. User Not Found (404):
```json
{
  "status": 404,
  "message": "User not found"
}
```

2. Internal Server Error (500):
```json
{
  "status": 500,
  "message": "Internal server error: detailed message"
}
```

## Example Usage

Using cURL:
```bash
curl http://localhost:8080/api/users/octocat/repositories
```

Using HTTPie:
```bash
http GET http://localhost:8080/api/users/octocat/repositories
```

## Running Tests
Execute the test suite with:
```bash
./mvnw test
```
