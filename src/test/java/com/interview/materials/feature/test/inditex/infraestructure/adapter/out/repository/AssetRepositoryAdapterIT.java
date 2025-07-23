package com.interview.materials.feature.test.inditex.infraestructure.adapter.out.repository;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.model.AssetId;
import com.interview.materials.feature.test.inditex.infraestructure.db.entity.AssetEntity;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class AssetRepositoryAdapterIT {

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
    private AssetRepositoryAdapter assetRepositoryAdapter;

    @Autowired
    private R2dbcEntityTemplate template;

    @Autowired
    private AssetMapper assetMapper;

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime yesterday = now.minusDays(1);
    private final LocalDateTime lastWeek = now.minusWeeks(1);

    @BeforeEach
    void setUp() {
        template.delete(AssetEntity.class)
                .all()
                .then().block();

        AssetEntity document = createAssetEntity("document.pdf", "application/pdf", yesterday);
        AssetEntity image = createAssetEntity("image.png", "image/png", now);
        AssetEntity report = createAssetEntity("report.pdf", "application/pdf", lastWeek);

        template.insert(AssetEntity.class)
                .using(document)
                .then(template.insert(AssetEntity.class).using(image))
                .then(template.insert(AssetEntity.class).using(report))
                .block();
    }

    private AssetEntity createAssetEntity(String filename, String contentType, LocalDateTime uploadDate) {
        return AssetEntity.builder()
                .id(UUID.randomUUID())
                .filename(filename)
                .contentType(contentType)
                .url("https://assets.cdn.fake/" + filename)
                .size(1024L)
                .uploadDate(uploadDate)
                .build();
    }

    @Test
    void save_shouldPersistAsset() {
        Asset newAsset = Asset.builder()
                .id(AssetId.of(UUID.randomUUID()))
                .filename("new.pdf")
                .contentType("application/pdf")
                .url("https://assets.cdn.fake/new.pdf")
                .size(2048L)
                .uploadDate(now)
                .build();

        Mono<Asset> savedAsset = assetRepositoryAdapter.save(newAsset);

        StepVerifier.create(savedAsset)
                .assertNext(asset -> {
                    assertThat(asset.getId()).isNotNull();
                    assertThat(asset.getFilename()).isEqualTo("new.pdf");
                    assertThat(asset.getContentType()).isEqualTo("application/pdf");
                })
                .verifyComplete();

        StepVerifier.create(template.select(AssetEntity.class)
                        .count())
                .assertNext(count -> assertThat(count).isEqualTo(4))
                .verifyComplete();
    }

    @Test
    void findByFilters_withoutFilters_shouldReturnAllAssets() {
        Flux<Asset> assets = assetRepositoryAdapter.findByFilters(null, null, null, null, null);

        StepVerifier.create(assets.collectList())
                .assertNext(list -> {
                    assertThat(list).hasSize(3);
                    assertThat(list).extracting(Asset::getFilename)
                            .containsExactlyInAnyOrder("document.pdf", "image.png", "report.pdf");
                })
                .verifyComplete();
    }

    @Test
    void findByFilters_withFilenameFilter_shouldReturnMatchingAssets() {
        Flux<Asset> assets = assetRepositoryAdapter.findByFilters("image.png", null, null, null, null);

        StepVerifier.create(assets.collectList())
                .assertNext(list -> {
                    assertThat(list).hasSize(1);
                    assertThat(list.get(0).getFilename()).isEqualTo("image.png");
                    assertThat(list.get(0).getContentType()).isEqualTo("image/png");
                })
                .verifyComplete();
    }

    @Test
    void findByFilters_withContentTypeFilter_shouldReturnMatchingAssets() {
        Flux<Asset> assets = assetRepositoryAdapter.findByFilters(null, "application/pdf", null, null, null);

        StepVerifier.create(assets.collectList())
                .assertNext(list -> {
                    assertThat(list).hasSize(2);
                    assertThat(list).extracting(Asset::getFilename)
                            .containsExactlyInAnyOrder("document.pdf", "report.pdf");
                })
                .verifyComplete();
    }

    @Test
    void findByFilters_withDateRangeFilter_shouldReturnAssetsInRange() {
        Flux<Asset> assets = assetRepositoryAdapter.findByFilters(
                null,
                null,
                now.minusDays(1),
                now.plusDays(1),
                null);

        StepVerifier.create(assets.collectList())
                .assertNext(list -> {
                    assertThat(list).hasSize(2);
                    assertThat(list).extracting(Asset::getFilename)
                            .containsExactlyInAnyOrder("document.pdf", "image.png");
                })
                .verifyComplete();
    }

    @Test
    void findByFilters_withSortDirection_shouldReturnSortedAssets() {
        Flux<Asset> assets = assetRepositoryAdapter.findByFilters(null, null, null, null, "DESC");

        StepVerifier.create(assets.collectList())
                .assertNext(list -> {
                    assertThat(list).hasSize(3);
                    assertThat(list).extracting(Asset::getFilename)
                            .containsExactly("image.png", "document.pdf", "report.pdf");
                })
                .verifyComplete();
    }

    @Test
    void findByFilters_withAllFilters_shouldReturnCorrectResults() {
        Flux<Asset> assets = assetRepositoryAdapter.findByFilters(
                "%.pdf",
                "application/pdf",
                lastWeek.minusDays(1),
                now.plusDays(1),
                "ASC");

        StepVerifier.create(assets.collectList())
                .assertNext(list -> {
                    assertThat(list).hasSize(2);
                    assertThat(list).extracting(Asset::getFilename)
                            .containsExactly("report.pdf", "document.pdf");
                })
                .verifyComplete();
    }
}