package com.interview.materials.feature.test.inditex.infraestructure.rest;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.model.AssetId;
import com.interview.materials.feature.test.inditex.infraestructure.adapter.out.repository.AssetRepositoryAdapter;
import com.interview.materials.feature.test.inditex.infraestructure.repos.r2dbc.AssetEntityRepositoryR2dbc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AssetGetControllerIT {

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

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setUp() {
        assetEntityRepositoryR2dbc.deleteAll()
                .thenMany(insertTestData())
                .blockLast();
    }

    private Flux<Asset> insertTestData() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime lastWeek = now.minusWeeks(1);

        return Flux.merge(
                assetRepositoryAdapter.save(createAsset("document.pdf", "application/pdf", yesterday)),
                assetRepositoryAdapter.save(createAsset("image.png", "image/png", now)),
                assetRepositoryAdapter.save(createAsset("report.pdf", "application/pdf", lastWeek)),
                assetRepositoryAdapter.save(createAsset("profile.jpg", "image/jpeg", yesterday.minusHours(2)))
        );
    }

    private Asset createAsset(String filename, String contentType, LocalDateTime uploadDate) {
        return Asset.builder()
                .id(AssetId.of(UUID.randomUUID()))
                .filename(filename)
                .contentType(contentType)
                .url("https://assets.cdn.fake/" + filename)
                .size(1024L)
                .uploadDate(uploadDate)
                .build();
    }

    @Test
    void getAssets_WithoutFilters_ReturnsAllAssets() {
        webTestClient.get()
                .uri("/api/mgmt/1/assets")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Asset.class)
                .hasSize(4);
    }

    @Test
    void getAssets_WithFilenameFilter_ReturnsMatchingAssets() {
        webTestClient.get()
                .uri("/api/mgmt/1/assets?filename=document.pdf")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Asset.class)
                .hasSize(1)
                .value(assets -> {
                    assertThat(assets.get(0).getFilename()).isEqualTo("document.pdf");
                    assertThat(assets.get(0).getContentType()).isEqualTo("application/pdf");
                });
    }

    @Test
    void getAssets_WithFiletypeFilter_ReturnsMatchingAssets() {
        webTestClient.get()
                .uri("/api/mgmt/1/assets?filetype=image/png")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Asset.class)
                .hasSize(1)
                .value(assets -> {
                    assertThat(assets.get(0).getFilename()).isEqualTo("image.png");
                    assertThat(assets.get(0).getContentType()).isEqualTo("image/png");
                });
    }

    @Test
    void getAssets_WithDateRangeFilter_ReturnsAssetsInRange() {
        String start = LocalDateTime.now().minusDays(2).format(dateFormatter);
        String end = LocalDateTime.now().plusDays(1).format(dateFormatter);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/mgmt/1/assets")
                        .queryParam("uploadDateStart", start)
                        .queryParam("uploadDateEnd", end)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Asset.class)
                .hasSize(3)
                .value(assets -> {
                    assertThat(assets).extracting(Asset::getFilename)
                            .containsExactlyInAnyOrder("document.pdf", "image.png", "profile.jpg");
                });
    }

    @Test
    void getAssets_WithSortDirection_ReturnsSortedAssets() {
        webTestClient.get()
                .uri("/api/mgmt/1/assets?sortDirection=DESC")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Asset.class)
                .value(assets -> {
                    assertThat(assets).extracting(Asset::getFilename)
                            .containsExactly("image.png", "document.pdf", "profile.jpg", "report.pdf");
                });
    }

    @Test
    void getAssets_WithInvalidSortDirection_ReturnsBadRequest() {
        webTestClient.get()
                .uri("/api/mgmt/1/assets?sortDirection=INVALID")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").value(message ->
                        assertThat(message).asString().contains("Invalid sort direction"));
    }

    @Test
    void getAssets_WithInvalidEndpointCall_ReturnsNotFoundError() {
        webTestClient.get()
                .uri("/api/mgmt/2/assets?sortDirection=ASC")
                .exchange()
                .expectStatus().isNotFound();
    }
}