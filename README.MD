# Interview Materials Feature Test Inditex

## Overview

This project is an application designed to manage assets. It leverages modern technologies such as **Java**, **Spring Boot**, **R2DBC**, and **PostgreSQL** to provide a reactive and scalable solution. The application is containerized using **Docker** and includes comprehensive testing, logging, and error handling mechanisms.

---

## Architecture

The project follows a layered architecture:

1. **Controllers**: Handle HTTP requests and responses.
2. **Services**: Contain business logic.
3. **Use Cases**: Encapsulate specific application functionalities.
4. **Repositories**: Interact with the database using reactive programming.
5. **Infrastructure**: Includes configuration files and database initialization logic.

---

## Key Components

### Controllers

- **[`AssetGetController`](./src/main/java/com/interview/materials/feature/test/inditex/infraestructure/web/rest/AssetGetController.java)**: Handles GET requests to retrieve assets.
- **[`AssetPostController`](./src/main/java/com/interview/materials/feature/test/inditex/infraestructure/web/rest/AssetPostController.java)**: Handles POST requests to upload new assets.

### Services

- **[`GetAssetsService`](./src/main/java/com/interview/materials/feature/test/inditex/application/service/GetAssetsService.java)**: Provides logic to fetch assets from the database.
- **[`UploadAssetService`](./src/main/java/com/interview/materials/feature/test/inditex/application/service/UploadAssetService.java)**: Handles asset upload operations.

### Use Cases

- **[`GetAssetsByFilterUseCase`](./src/main/java/com/interview/materials/feature/test/inditex/application/usecase/GetAssetsByFilterUseCase.java)**: Implements filtering logic for asset retrieval.
- **[`UploadAssetUseCase`](./src/main/java/com/interview/materials/feature/test/inditex/application/usecase/UploadAssetUseCase.java)**: Encapsulates the asset upload process.

---

## Utilities

The [`DateParser`](./src/main/java/com/interview/materials/feature/test/inditex/shared/utils/DateParser.java) class provides utility methods for parsing and formatting dates. It ensures consistent handling of date formats across the application, particularly for filtering assets by date ranges. This utility is designed to handle edge cases and invalid date inputs gracefully.

---

## Traceability

The [`TraceIdHolder`](./src/main/java/com/interview/materials/feature/test/inditex/shared/context/TraceIdHolder.java) class is used to manage traceability across the application. It provides a mechanism to store and retrieve a unique `traceId` for each request, ensuring that logs and error responses can be correlated effectively. This enhances debugging and monitoring capabilities.

The [`TraceWebFilter`](./src/main/java/com/interview/materials/feature/test/inditex/shared/web/TraceWebFilter.java) integrates with **Spring WebFlux** to automatically generate and attach a `traceId` to incoming requests. This `traceId` is propagated throughout the application lifecycle, ensuring consistent traceability in logs and error handling.

---

## Error Handling

The application uses centralized [`GlobalErrorController`](./src/main/java/com/interview/materials/feature/test/inditex/infraestructure/web/error/GlobalErrorController.java) to log and respond to exceptions gracefully. Custom exceptions are logged using **SLF4J** and returned as meaningful HTTP responses.

### Error Codes

The following HTTP status codes are used across the application for error handling:

- **400 BAD REQUEST**:
  - `InvalidBase64EncodedAssetException`: Indicates that the provided asset is not properly Base64 encoded.
  - `UnsupportedAssetContentTypeException`: Indicates that the asset's content type is not supported.
  - `InvalidDateRangeException`: Indicates that the provided date range is invalid.
  - `InvalidSortDirectionException`: Indicates that the sort direction is invalid.
  - `WebExchangeBindException`: Indicates validation errors in the request, providing detailed field-level error messages.

- **404 NOT FOUND**:
  - `NoResourceFoundException`: Indicates that the requested resource could not be found.

- **500 INTERNAL SERVER ERROR**:
  - Unhandled exceptions or unexpected errors in the application are logged and returned with a generic error message.

### Error Response Format

The `ErrorResponse` class standardizes the structure of error responses, including:
- HTTP status code and reason.
- Error message.
- Timestamp of the error.
- Optional field-level validation errors.

This approach ensures consistent and informative error handling across the application.

---

## Logging

Logging is configured to provide detailed insights into application behavior, ensuring traceability and efficient debugging. Key aspects include:

- **Spring R2DBC**: Logs database interactions, including executed queries and their performance metrics. These logs are set to `DEBUG` level for detailed analysis.

- **PostgreSQL Queries**: SQL queries executed by R2DBC are logged at `DEBUG` level, allowing developers to monitor query execution and identify potential issues.

- **Traceability**: Each log entry includes a unique `traceId`, generated by the `TraceWebFilter` and propagated throughout the application lifecycle. This ensures that logs can be correlated with specific requests for better debugging and monitoring.

- **Error Handling Logs**: Exceptions are logged with detailed information, including the `traceId`, HTTP status, and error message. This is implemented in the `GlobalErrorController` to provide consistent and informative error logging.

This logging setup ensures comprehensive visibility into application operations and facilitates troubleshooting in production environments.

---

## Database Initialization

The [`R2dbcSchemaInitializer`](./src/main/java/com/interview/materials/feature/test/inditex/infraestructure/db/config/R2dbcSchemaInitializer.java) class initializes the database schema using the [`schema.sql`](./src/main/resources/schema.sql) file. Errors during initialization are logged.

---

## Testing

The project includes unit and integration tests to ensure the reliability and correctness of the application. Testing is performed using **JUnit** and **Mockito**, and the coverage is measured using **JaCoCo**.

### Types of Tests

- **Unit Tests**: Validate the behavior of individual components, such as classes or methods, in isolation. These tests ensure that each unit of code works as expected.

- **Integration Tests**: Verify the interaction between multiple components of the system, such as controllers and repositories. These tests ensure that modules work together coherently.

### Test Coverage

- **JaCoCo** is configured to generate detailed reports on code coverage, including:
  - **Line Coverage**: Percentage of executed lines of code.
  - **Branch Coverage**: Percentage of executed branches in conditional statements.
  - **Method Coverage**: Percentage of executed methods.

The coverage reports are generated in the `target/site/jacoco` directory after running the tests. These reports can be visualized in HTML format for easy analysis.

### How to Run Tests

1. Execute the following Maven command to run all tests and generate the coverage report:

   ```bash
   mvn clean test verify
   ```

   ```bash
   target/site/jacoco/index.html
   ```

  ![img.png](img.png)

---

## Configuration

### Application Properties

The application uses different configuration files to manage environment-specific settings, ensuring flexibility for both local development and Docker-based deployments.

#### Local Configuration

For local development, the application uses [`application.yaml`](./src/main/resources/application.yaml). This file configures an in-memory H2 database in PostgreSQL compatibility mode, along with settings for management endpoints and detailed logging for debugging purposes.

#### Docker Configuration

For Docker-based deployments, the application uses [`application-docker.yaml`](./src/main/resources/application-docker.yaml). This file sets up the connection to a PostgreSQL database hosted in a Docker container. It also includes connection pooling settings to optimize performance in production environments.

#### Key Differences

- **Database**: Local configuration uses an H2 in-memory database, while Docker connects to PostgreSQL.

These configurations ensure the application operates seamlessly across different environments.

## How to Run

### Local

To run the application locally, use the following Maven command:

```bash
mvn spring-boot:run
```

This will start the application with the default application.yaml configuration, which uses an in-memory H2 database. The database will be initialized with test data from the [`schema.sql`](./src/main/resources/schema.sql) . The application will be accessible at http://localhost:8080.
You can check the application health status with:

```bash
curl http://localhost:8080/actuator/health
```

### Docker

To run the application in a Docker environment, use the following steps:

1. Build the Docker image:

   ```bash
   docker build -t asset-api .
   ```
2. Start the containers using docker-compose:

   ```bash
   docker-compose up -d
    ```
This will start the application with the docker profile, connecting to a PostgreSQL database container. The database will be initialized with test data from the [`schema.sql`](./src/main/resources/schema.sql) file. The application will be accessible at http://localhost:8080.
You can check the application health status with:

```bash
curl http://localhost:8080/actuator/health
```

### Postman Collection

The project includes a Postman collection [`AssetCollection.postman_collection.json`](./AssetCollection.postman_collection.json) to test API endpoints. It contains:
- **GET /assets**: Retrieve assets.
- **POST /assets**: Upload new assets.

## Future Improvements

- **Resilient**: Implement retry mechanisms and circuit breakers using libraries like **Resilience4j** to handle transient failures and improve system reliability. Add fallback methods to ensure graceful degradation during service interruptions.
- **Security**: Enhance security by integrating **Spring Security** for authentication and authorization. Implement token-based authentication (e.g., **JWT**) and secure sensitive endpoints with role-based access control.
- **Pagination**: Add pagination support to the API responses to handle large datasets efficiently. Use query parameters like page and size to control the pagination behavior.
- **OpenAPI Definition**: Review and improve the openapi.yml file to ensure it accurately reflects the API structure and includes detailed descriptions, examples, and proper response codes for each endpoint.

### OpenAPI Definition Enhancements
1. Avoid Returning Raw Entities from Endpoints:
Directly returning entities from endpoints such as GET /api/mgmt/1/assets/ is considered bad practice. Instead, it's recommended to wrap the returned data in a response object, which provides greater flexibility and extensibility (e.g., for future pagination or metadata).

**Before:**
```yaml 
responses:
  '200':
    description: Returns a list of assets matching the specified criteria.
    content:
      application/json:
        schema:
          type: array
          items:
            $ref: '#/components/schemas/Asset'
```
**After:**
```yaml 
responses:
  '200':
    description: Returns a list of assets matching the specified criteria.
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/PaginatedAssetResponse'
```
**New Schema:**
```yaml 
    PaginatedAssetResponse:
      type: object
      description: A paginated response containing a list of assets.
      properties:
        content:
          type: array
          items:
            $ref: '#/components/schemas/Asset'
        page:
          type: integer
          description: Current page number (zero-based).
          example: 0
        size:
          type: integer
          description: Number of items per page.
          example: 20
        totalElements:
          type: integer
          description: Total number of elements matching the filter.
          example: 145
        totalPages:
          type: integer
          description: Total number of pages.
          example: 8
        last:
          type: boolean
          description: Whether this is the last page.
          example: false
        first:
          type: boolean
          description: Whether this is the first page.
          example: true
        numberOfElements:
          type: integer
          description: Number of elements in the current page.
          example: 20
```
2. Use Proper Date Format:
Current date parameters are typed as plain `string`. To improve precision and allow automatic validation, use `format: date-time` for all date-related fields:

```yaml
UploadDateStart:
  in: query
  name: uploadDateStart
  description: The start date for the range.
  schema:
    type: string
    format: date-time
UploadDateEnd:
  in: query
  name: uploadDateEnd
  description: The end date for the range.
  schema:
    type: string
    format: date-time
```
3. Declare Required Fields in Schemas:
Ensure that all necessary fields are explicitly marked as `required` to enforce validation and improve Swagger documentation:

```yaml
AssetFileUploadRequest:
  type: object
  description: Represents a file upload request.
  required:
    - filename
    - encodedFile
    - contentType
  properties:
    filename:
      type: string
      format: string
      example: img.png
      description: The filename associated to the asset being uploaded.
    encodedFile:
      type: string
      format: byte
      description: The actual file to be uploaded.
    contentType:
      type: string
      description: The filetype according to the MIME type (IANA) definition.
```

4. Standardize Endpoint Naming:
Prefer RESTFULL naming conventions:
- Use `/assets` instead of `/assets/` (no trailing slash) for consistency and clarity.

5. Provide Complete and Realistic Examples:
Enhance the documentation by including fully populated example objects in schemas, such as:

```yaml
Asset:
  example:
    id: "123e4567-e89b-12d3-a456-426614174000"
    filename: "img.png"
    contentType: "image/png"
    url: "https://cdn.example.com/assets/img.png"
    size: 2048
    uploadDate: "2025-07-22T10:00:00Z"
```
6. Include Detailed Error Response Examples:
Provide clear examples of error responses for each HTTP status code. This improves client-side integration and error handling:

```yaml
responses:
    '202':
      description: The operation was accepted by the backend.
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/AssetFileUploadResponse'
    '400':
      description: Bad request due to validation errors
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
      examples:
        InvalidBase64:
          value:
            status: 400
            error: Bad Request
            message: The encoded file is not valid base64.
            timestamp: "2025-07-22 10:00:00"
            invalidParams: null
        UnsupportedContentType:
          value:
            status: 400
            error: Bad Request
            message: "Unsupported content type: other/png"
            timestamp: "2025-07-22 10:00:00"
            invalidParams: null
        FieldValidation:
          value:
            status: 400
            error: Bad Request
            message: One or more fields are invalid
            timestamp: "2025-07-22 10:00:00"
            errors:
              - field: filename
                message: filename cannot be blank
              - field: contentType
                message: contentType cannot be blank
    '404':
      description: Resource not found
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
    '500':
      description: An unexpected error occurred.
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
```

#### Additional Recommendations
- **Pagination**: Introduce pagination parameters (`page`, `size`) to improve performance and usability when handling large datasets:
```yaml
parameters:
  - name: page
    in: query
    description: Page number to retrieve.
    schema:
      type: integer
      default: 0
  - name: size
    in: query
    description: Number of items per page.
    schema:
      type: integer
      default: 20
```
- **Security**: Secure API endpoints using token-based authentication (e.g., JWT). This ensures only authorized users can access or modify protected resources.
1. Define the security scheme under components.securitySchemes:
```yaml
components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```
2. Then, secure endpoints by adding security requirements:
```yaml
/api/mgmt/1/assets/actions/upload:
  post:
    tags:
      - asset
    summary: Performs an upload of the requested asset file.
    description: Performs an upload of the requested asset file.
    operationId: uploadAssetFile
    security:
      - BearerAuth: []
    requestBody:
      required: true
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/AssetFileUploadRequest'
    responses:
      '202':
        description: The operation was accepted by the backend.
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AssetFileUploadResponse'
      '400':
        description: Bad request due to validation errors
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ErrorResponse'
          examples:
            InvalidBase64:
              value:
                status: 400
                error: Bad Request
                message: The encoded file is not valid base64.
                timestamp: "2025-07-22 10:00:00"
                invalidParams: null
            UnsupportedContentType:
              value:
                status: 400
                error: Bad Request
                message: "Unsupported content type: other/png"
                timestamp: "2025-07-22 10:00:00"
                invalidParams: null
            FieldValidation:
              value:
                status: 400
                error: Bad Request
                message: One or more fields are invalid
                timestamp: "2025-07-22 10:00:00"
                errors:
                  - field: filename
                    message: filename cannot be blank
                  - field: contentType
                    message: contentType cannot be blank
      '401':
        description: Unauthorized – missing or invalid token
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ErrorResponse'
      '403':
        description: Forbidden – insufficient permissions
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ErrorResponse'
      '404':
        description: Resource not found
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ErrorResponse'
      '500':
        description: An unexpected error occurred.
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ErrorResponse'
```
- **Swagger**: The application uses **Swagger** to document the API endpoints. The OpenAPI specification is available in the `openapi.yml` file, which can be used to generate interactive API documentation.
- **Contract First**: The OpenAPI specification is designed to be used as a contract-first approach, allowing developers to define the API structure before implementation. This ensures that the API meets the expected requirements and facilitates collaboration between frontend and backend teams.

[`OpenApi Version 1.0.0`](./src/main/resources/static/openapi-v1.yaml)
[`OpenApi Version 2.0.0`](./src/main/resources/static/openapi-v2.yaml)