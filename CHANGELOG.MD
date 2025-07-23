# Changelog

## [1.0.0] – 2025-07-23
### Added
- Initial draft:
    - Project skeleton with domain modules and adapters.
    - Base dependencies for Spring Boot and JPA.
    - Skeleton of `openapi.yml` imported as a resource.
    - Configuration for `local` and `docker` profiles.
- Asset upload feature:
    - Asynchronous file upload process management.
    - Metadata persistence for uploaded files (UUID, original name, size, date, status).
- REST API based on `openapi.yml` specification:
    - `POST /assets/upload` endpoint to initiate asynchronous uploads.
    - `GET /assets/{id}` endpoint to retrieve metadata of a specific asset.
    - `GET /assets` endpoint for searching and filtering persisted assets:
        - Sorting filter (`sort` by date, name, size).
- Hexagonal architecture:
    - Separation into domain, application (use cases), and adapter layers (REST, repositories).
- Persistence with JPA/Hibernate (relational database).
- Java 17/21 and Spring Boot 3.x.
- Unit tests with JUnit 5 and Mockito.
- Integration tests with Spring Boot Test and TestContainers.
- Docker support:
    - Dockerfile for building the application image.
    - Docker Compose file for running the application with a PostgreSQL database.
- Postman Collection for API testing.
- OpenAPI documentation generated from `openapi.yml`. Added `openapi-v2.yml` with improvements.

### Changed
- **(N/A)**

### Deprecated
- **(N/A)**

### Removed
- **(N/A)**

### Fixed
- **(N/A)**

### Security
- **(N/A)**