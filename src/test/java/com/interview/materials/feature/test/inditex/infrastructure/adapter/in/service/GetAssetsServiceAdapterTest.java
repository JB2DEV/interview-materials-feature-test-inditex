package com.interview.materials.feature.test.inditex.infrastructure.adapter.in.service;

import com.interview.materials.feature.test.inditex.application.command.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidDateRangeException;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidSortDirectionException;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.model.AssetId;
import com.interview.materials.feature.test.inditex.domain.port.in.usecase.GetAssetsByFilterUseCasePort;
import com.interview.materials.feature.test.inditex.shared.enums.SortDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssetsServiceAdapterTest {

    @Mock
    private GetAssetsByFilterUseCasePort getAssetsByFilterUseCasePort;

    @Mock
    private AssetValidator assetValidator;

    @InjectMocks
    private GetAssetsServiceAdapter getAssetsServiceAdapter;

    private FindAssetsByFiltersCommand createValidCommand() {
        return FindAssetsByFiltersCommand.builder()
                .filename("test.jpg")
                .contentType("image/jpeg")
                .uploadDateStart(LocalDateTime.parse("2024-01-01T00:00:00"))
                .uploadDateEnd(LocalDateTime.parse("2024-12-31T00:00:00"))
                .sortDirection(SortDirection.ASC)
                .build();
    }

    private Asset createTestAsset() {
        return new Asset(
                AssetId.of(UUID.randomUUID()),
                "test.jpg",
                "image/jpeg",
                "https://cdn.fake/test.jpg",
                1024L,
                LocalDateTime.now()
        );
    }

    @Test
    void find_shouldReturnAssetsWhenRequestIsValid() {
        FindAssetsByFiltersCommand command = createValidCommand();
        Asset testAsset = createTestAsset();

        when(assetValidator.validateSortDirection("ASC")).thenReturn(Mono.empty());
        when(assetValidator.validateDateRange(
                command.uploadDateStart(),
                command.uploadDateEnd()
        )).thenReturn(Mono.empty());

        when(getAssetsByFilterUseCasePort.find(command)).thenReturn(Flux.just(testAsset));

        StepVerifier.create(getAssetsServiceAdapter.find(command))
                .expectNext(testAsset)
                .verifyComplete();

        verify(assetValidator).validateSortDirection("ASC");
        verify(assetValidator).validateDateRange(command.uploadDateStart(), command.uploadDateEnd());
        verify(getAssetsByFilterUseCasePort).find(command);
    }

    @Test
    void find_shouldWorkWithNullDates() {
        FindAssetsByFiltersCommand command = FindAssetsByFiltersCommand.builder()
                .filename("test.jpg")
                .contentType("image/jpeg")
                .uploadDateStart(null)
                .uploadDateEnd(null)
                .sortDirection(SortDirection.ASC)
                .build();

        Asset testAsset = createTestAsset();

        when(assetValidator.validateSortDirection("ASC")).thenReturn(Mono.empty());
        when(getAssetsByFilterUseCasePort.find(command)).thenReturn(Flux.just(testAsset));

        StepVerifier.create(getAssetsServiceAdapter.find(command))
                .expectNext(testAsset)
                .verifyComplete();

        verify(assetValidator, never()).validateDateRange(any(), any());
    }

    @Test
    void find_shouldPropagateSortValidationError() {
        FindAssetsByFiltersCommand command = FindAssetsByFiltersCommand.builder()
                .filename("test.jpg")
                .contentType("image/jpeg")
                .sortDirection(null)
                .build();

        when(assetValidator.validateSortDirection("null"))
                .thenReturn(Mono.error(
                        new InvalidSortDirectionException("Invalid sort direction: null")
                ));

        StepVerifier.create(getAssetsServiceAdapter.find(command))
                .expectErrorMatches(ex ->
                        ex instanceof InvalidSortDirectionException &&
                                ex.getMessage().contains("Invalid sort direction: null")
                )
                .verify();

        verify(assetValidator).validateSortDirection("null");
        verify(assetValidator, never()).validateDateRange(any(), any());
    }

    @Test
    void find_shouldPropagateDateValidationError() {
        LocalDateTime start = LocalDateTime.parse("2024-12-31T00:00:00");
        LocalDateTime end = LocalDateTime.parse("2024-01-01T00:00:00");

        FindAssetsByFiltersCommand command = FindAssetsByFiltersCommand.builder()
                .filename("test.jpg")
                .contentType("image/jpeg")
                .uploadDateStart(start)
                .uploadDateEnd(end)
                .sortDirection(SortDirection.ASC)
                .build();

        when(assetValidator.validateSortDirection("ASC")).thenReturn(Mono.empty());
        when(assetValidator.validateDateRange(start, end))
                .thenReturn(Mono.error(
                        new InvalidDateRangeException("Invalid date range")
                ));

        StepVerifier.create(getAssetsServiceAdapter.find(command))
                .expectError(InvalidDateRangeException.class)
                .verify();

        verify(assetValidator).validateSortDirection("ASC");
        verify(assetValidator).validateDateRange(start, end);
    }


    @Test
    void find_shouldHandleEmptyResult() {
        FindAssetsByFiltersCommand command = createValidCommand();

        when(assetValidator.validateSortDirection("ASC")).thenReturn(Mono.empty());
        when(assetValidator.validateDateRange(
                command.uploadDateStart(),
                command.uploadDateEnd()
        )).thenReturn(Mono.empty());

        when(getAssetsByFilterUseCasePort.find(command)).thenReturn(Flux.empty());

        StepVerifier.create(getAssetsServiceAdapter.find(command))
                .expectNextCount(0)
                .verifyComplete();
    }
}