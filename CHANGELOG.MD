# Changelog
## [1.1.0] – 2025-07-24
### Added
- Security configuration for JWT authentication.
- LoginUseCase for user authentication.
- User entity and repository for managing user data.
- JWT token generation and validation.
### Changed
- Updated AssetPostRestControllerAdapter to include JWT authentication.

## [1.0.1] – 2025-07-23
### Added
- Validation pattern on `AssetFileUploadRequest` for Base64 encoding via `@Pattern` annotation
### Changed
- Reorganized into clear Domain, Application, and Infrastructure modules following Hexagonal Architecture with `ports` and `adapters`
### Removed
- Removed `InvalidBase64EncodedAssetException`
- Removed redundant manual bean wiring in `AssetConfig`, relying instead on automatic component scanning for all infrastructure adapters.

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