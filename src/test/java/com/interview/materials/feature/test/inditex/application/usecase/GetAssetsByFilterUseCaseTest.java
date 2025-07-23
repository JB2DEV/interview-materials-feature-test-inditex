package com.interview.materials.feature.test.inditex.application.usecase;

import com.interview.materials.feature.test.inditex.application.command.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.port.out.repository.AssetRepositoryPort;
import com.interview.materials.feature.test.inditex.shared.enums.SortDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssetsByFilterUseCaseTest {

    @Mock
    private AssetRepositoryPort assetRepositoryPort;

    @InjectMocks
    private GetAssetsByFilterUseCase getAssetsByFilterUseCaseImpl;

    @Test
    void find_shouldReturnAssets_whenFiltersMatch() {
        LocalDateTime now = LocalDateTime.now();
        FindAssetsByFiltersCommand command = new FindAssetsByFiltersCommand(
                "test.png",
                "image/png",
                now.minusDays(1),
                now.plusDays(1),
                SortDirection.ASC
        );

        Asset asset1 = Asset.builder()
                .filename("test.png")
                .contentType("image/png")
                .uploadDate(now)
                .build();

        Asset asset2 = Asset.builder()
                .filename("test.png")
                .contentType("image/png")
                .uploadDate(now.minusHours(1))
                .build();

        when(assetRepositoryPort.findByFilters(
                anyString(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyString()))
                .thenReturn(Flux.just(asset1, asset2));

        StepVerifier.create(getAssetsByFilterUseCaseImpl.find(command))
                .expectNext(asset1)
                .expectNext(asset2)
                .verifyComplete();
    }

    @Test
    void find_shouldCallRepositoryWithCorrectParameters() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        FindAssetsByFiltersCommand command = new FindAssetsByFiltersCommand(
                "test.png",
                "image/png",
                startDate,
                endDate,
                SortDirection.DESC
        );

        when(assetRepositoryPort.findByFilters(any(), any(), any(), any(), any()))
                .thenReturn(Flux.empty());

        getAssetsByFilterUseCaseImpl.find(command).blockLast();

        verify(assetRepositoryPort).findByFilters(
                "test.png",
                "image/png",
                startDate,
                endDate,
                "DESC");
    }

    @Test
    void find_shouldHandleNullFilters() {
        FindAssetsByFiltersCommand command = new FindAssetsByFiltersCommand(
                null,
                null,
                null,
                null,
                null
        );

        Asset asset = Asset.builder().filename("any.png").build();

        when(assetRepositoryPort.findByFilters(
                any(),
                any(),
                any(),
                any(),
                any()))
                .thenReturn(Flux.just(asset));

        StepVerifier.create(getAssetsByFilterUseCaseImpl.find(command))
                .expectNext(asset)
                .verifyComplete();
    }

    @Test
    void find_shouldReturnEmptyFlux_whenNoAssetsFound() {
        FindAssetsByFiltersCommand command = new FindAssetsByFiltersCommand(
                "not-found.png",
                "image/png",
                null,
                null,
                SortDirection.ASC
        );

        when(assetRepositoryPort.findByFilters(any(), any(), any(), any(), any()))
                .thenReturn(Flux.empty());

        StepVerifier.create(getAssetsByFilterUseCaseImpl.find(command))
                .expectNextCount(0)
                .verifyComplete();
    }
}