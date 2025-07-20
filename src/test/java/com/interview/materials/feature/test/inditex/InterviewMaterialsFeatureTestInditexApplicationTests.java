package com.interview.materials.feature.test.inditex;

import com.interview.materials.feature.test.inditex.infraestructure.repos.r2dbc.AssetEntityRepositoryR2dbc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class InterviewMaterialsFeatureTestInditexApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15-alpine"))
            .withDatabaseName("context-test-db")
            .withUsername("r2dbc-user")
            .withPassword("r2dbc-pass");

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

    @Autowired(required = false)
    private AssetEntityRepositoryR2dbc assetEntityRepository;

    @Test
    void contextLoads() {
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void r2dbcRepositoriesAreConfigured() {
        assertThat(assetEntityRepository).isNotNull();
    }
}