# COOK WE

## Description

COOK WE is a simple REST API for a cookbook, developed using Spring Boot with security measures in place. It focuses on adhering to the DTO (Data Transfer Object) layer for improved data management.

Users can be created with specific roles and permissions. These users are able to create recipes, add recipes to their favorites, and comment on recipes.

## Installation

To install COOK WE, follow these steps:

1. Clone the repository:
    ```bash
    git clone https://github.com/quent36987/cook-we.git
    cd cook-we/docker
    ```

2. Start the Docker containers:
    ```bash
    docker-compose -f docker-compose.all.yml up -d
    ```

The API is then accessible at http://localhost:9001/api/...

## Pipeline

The pipeline includes testing the code, building it using Gradle, pushing the Docker image to the GitLab registry, and finally deploying the Swagger documentation to GitHub Pages.

You can access the Swagger documentation at [COOK WE Swagger Documentation](https://quent36987.github.io/cook-we/).

## Dependencies

- SDK: OpenJDK 17 (17-temurin)
- Spring Boot: 3.1.5

## Usage

Once the API is installed and running, you can interact with it using any HTTP client or tool of your choice. The provided endpoints allow for user management, recipe creation, favoriting recipes, and commenting on recipes.

---
### License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.


