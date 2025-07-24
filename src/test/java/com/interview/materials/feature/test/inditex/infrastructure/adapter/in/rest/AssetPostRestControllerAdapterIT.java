package com.interview.materials.feature.test.inditex.infrastructure.adapter.in.rest;

import com.interview.materials.feature.test.inditex.infrastructure.adapter.out.repository.AssetRepositoryAdapter;
import com.interview.materials.feature.test.inditex.infrastructure.adapter.out.repository.r2dbc.AssetEntityRepositoryR2dbc;
import com.interview.materials.feature.test.inditex.infrastructure.web.dto.AssetFileUploadRequest;
import com.interview.materials.feature.test.inditex.infrastructure.web.dto.AssetFileUploadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebTestClient
class AssetPostRestControllerAdapterIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15-alpine"))
            .withDatabaseName("assetdb")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void registerR2dbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:postgresql://%s:%d/%s",
                        postgres.getHost(),
                        postgres.getFirstMappedPort(),
                        postgres.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AssetRepositoryAdapter assetRepositoryAdapter;

    @Autowired
    private AssetEntityRepositoryR2dbc assetEntityRepositoryR2dbc;

    private String validToken;

    @BeforeEach
    void setUp() {
        assetEntityRepositoryR2dbc.deleteAll().block();
        validToken = getToken();
    }

    private String getToken() {
        return webTestClient.post()
                .uri("/api/mgmt/1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\": \"admin\", \"password\": \"admin123\"}")
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<Map<String, String>>() {})
                .getResponseBody()
                .map(map -> map.get("token"))
                .blockFirst();
    }

    private WebTestClient.RequestHeadersSpec<?> authenticate(
            WebTestClient.RequestHeadersSpec<?> spec
    ) {
        return spec.header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken);
    }

    @Test
    void uploadAsset_WithValidRequest_ReturnsAcceptedAndSavesToDatabase() {
        String validBase64Image = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAFhQHQwUJHzQAAAABJRU5ErkJggg==";
        AssetFileUploadRequest request = new AssetFileUploadRequest(
                "test.png",
                validBase64Image,
                "image/png"
        );

        int expectedSize = Base64.getDecoder().decode(validBase64Image).length;

        WebTestClient.RequestHeadersSpec<?> spec = webTestClient.post()
                .uri("/api/mgmt/1/assets/actions/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request);

        WebTestClient.RequestHeadersSpec<?> withAuth = authenticate(spec);

        withAuth
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(AssetFileUploadResponse.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();

                    UUID id = UUID.fromString(response.id());
                    StepVerifier.create(assetEntityRepositoryR2dbc.findById(id))
                            .assertNext(asset -> {
                                assertThat(asset.getFilename()).isEqualTo("test.png");
                                assertThat(asset.getContentType()).isEqualTo("image/png");
                                assertThat(asset.getUrl()).contains("https://assets.cdn.fake/test.png");
                                assertThat(asset.getSize()).isEqualTo(expectedSize);
                            })
                            .verifyComplete();
                });
    }

    @Test
    void uploadAsset_WithBlankFields_ReturnsBadRequest() {
        AssetFileUploadRequest request = new AssetFileUploadRequest(
                "",
                "",
                ""
        );

        WebTestClient.RequestHeadersSpec<?> spec = webTestClient.post()
                .uri("/api/mgmt/1/assets/actions/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request);

        WebTestClient.RequestHeadersSpec<?> withAuth = authenticate(spec);

        withAuth
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(message ->
                        assertThat(message).asString().contains("One or more fields are invalid"));
    }

    @Test
    void uploadAsset_WithInvalidContentType_ReturnsBadRequest() {
        AssetFileUploadRequest request = new AssetFileUploadRequest(
                "test.txt",
                "dGVzdCBjb250ZW50",
                "text/plain"
        );

        WebTestClient.RequestHeadersSpec<?> spec = webTestClient.post()
                .uri("/api/mgmt/1/assets/actions/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request);

        WebTestClient.RequestHeadersSpec<?> withAuth = authenticate(spec);

        withAuth
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(message ->
                        assertThat(message).asString().contains("Unsupported content type"));
    }

    @Test
    void uploadAsset_WithInvalidBase64_ReturnsBadRequest() {
        AssetFileUploadRequest request = new AssetFileUploadRequest(
                "test.png",
                "invalid-base64-data",
                "image/png"
        );

        WebTestClient.RequestHeadersSpec<?> spec = webTestClient.post()
                .uri("/api/mgmt/1/assets/actions/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request);

        WebTestClient.RequestHeadersSpec<?> withAuth = authenticate(spec);

        withAuth
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.invalidParams[0].message").value(message ->
                        assertThat(message).asString().contains("Invalid base64 encoding"));
    }

    @Test
    void uploadAsset_WithInvalidEndpointCall_ReturnsNotFoundError() {
        AssetFileUploadRequest request = new AssetFileUploadRequest(
                "test.txt",
                "dGVzdCBjb250ZW50",
                "text/plain"
        );

        WebTestClient.RequestHeadersSpec<?> spec = webTestClient.post()
                .uri("/api/mgmt/2/assets/actions/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request);

        WebTestClient.RequestHeadersSpec<?> withAuth = authenticate(spec);

        withAuth
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void uploadAsset_InvalidToken_ReturnsUnauthorized() {
        AssetFileUploadRequest req = new AssetFileUploadRequest(
                "new-image.jpg",
                Base64.getEncoder().encodeToString("bytes".getBytes()),
                "image/jpg"
        );

        webTestClient.post()
                .uri("/api/mgmt/1/assets/actions/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + "bad.token.here")
                .bodyValue(req)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void uploadAsset_NoToken_ReturnsUnauthorized() {
        AssetFileUploadRequest req = new AssetFileUploadRequest(
                "new-image.jpg",
                Base64.getEncoder().encodeToString("bytes".getBytes()),
                "image/jpg"
        );

        webTestClient.post()
                .uri("/api/mgmt/1/assets/actions/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenUserHasOnlyRoleUser_thenUploadReturnsForbiddenWithCustomBody() {
        AssetFileUploadRequest req = new AssetFileUploadRequest(
                "forbidden.png",
                Base64.getEncoder().encodeToString("xxx".getBytes()),
                "image/png"
        );

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser("someUser").roles("USER"))
                .post().uri("/api/mgmt/1/assets/actions/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.message").isEqualTo("You do not have enough permissions");

    }
}