package com.interview.materials.feature.test.inditex.application.service;

import com.interview.materials.feature.test.inditex.application.usecase.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.application.usecase.GetAssetsByFilterUseCase;
import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidDateRangeException;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidSortDirectionException;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.model.AssetId;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFilterRequest;
import com.interview.materials.feature.test.inditex.shared.enums.SortDirection;
import org.junit.jupiter.api.BeforeEach;
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
class GetAssetsServiceTest {

    @Mock
    private GetAssetsByFilterUseCase getAssetsByFilterUseCase;

    @Mock
    private AssetValidator assetValidator;

    @Mock
    private AssetMapper assetMapper;

    @InjectMocks
    private GetAssetsService getAssetsService;

    private AssetFilterRequest request;
    private FindAssetsByFiltersCommand command;
    private Asset testAsset;

    @BeforeEach
    void setUp() {
        request = new AssetFilterRequest(
                "test.jpg",
                "image/jpeg",
                "2024-01-01T00:00:00",
                "2024-12-31T00:00:00",
                "ASC"
        );

        command = FindAssetsByFiltersCommand.builder()
                .filename("test.jpg")
                .contentType("image/jpeg")
                .uploadDateStart(LocalDateTime.parse("2024-01-01T00:00:00"))
                .uploadDateEnd(LocalDateTime.parse("2024-12-31T00:00:00"))
                .sortDirection(SortDirection.ASC)
                .build();

        testAsset = new Asset(
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
        when(assetMapper.toCommand(request)).thenReturn(command);
        when(assetValidator.validateSortDirection("ASC")).thenReturn(Mono.empty());
        when(assetValidator.validateDateRange(command.uploadDateStart(), command.uploadDateEnd())).thenReturn(Mono.empty());
        when(getAssetsByFilterUseCase.find(command)).thenReturn(Flux.just(testAsset));

        StepVerifier.create(getAssetsService.find(request))
                .expectNext(testAsset)
                .verifyComplete();

        verify(assetMapper).toCommand(request);
        verify(assetValidator).validateSortDirection("ASC");
        verify(assetValidator).validateDateRange(command.uploadDateStart(), command.uploadDateEnd());
        verify(getAssetsByFilterUseCase).find(command);
    }

    @Test
    void find_shouldWorkWithNullDates() {
        request = new AssetFilterRequest(
                "test.jpg",
                "image/jpeg",
                null,
                null,
                "ASC"
        );

        command = FindAssetsByFiltersCommand.builder()
                .filename("test.jpg")
                .contentType("image/jpeg")
                .uploadDateStart(null)
                .uploadDateEnd(null)
                .sortDirection(SortDirection.ASC)
                .build();

        when(assetMapper.toCommand(request)).thenReturn(command);
        when(assetValidator.validateSortDirection("ASC")).thenReturn(Mono.empty());
        when(getAssetsByFilterUseCase.find(command)).thenReturn(Flux.just(testAsset));

        StepVerifier.create(getAssetsService.find(request))
                .expectNext(testAsset)
                .verifyComplete();

        verify(assetValidator, never()).validateDateRange(any(), any());
    }

    @Test
    void find_shouldPropagateSortValidationError() {
        when(assetValidator.validateSortDirection("INVALID")).thenReturn(Mono.error(
                new InvalidSortDirectionException("Invalid sort direction")));

        request = new AssetFilterRequest(
                "test.jpg",
                "image/jpeg",
                null,
                null,
                "INVALID"
        );

        StepVerifier.create(getAssetsService.find(request))
                .expectError(InvalidSortDirectionException.class)
                .verify();

        verify(assetValidator).validateSortDirection("INVALID");
        verify(assetValidator, never()).validateDateRange(any(), any());
        verify(getAssetsByFilterUseCase, never()).find(any());
    }

    @Test
    void find_shouldPropagateDateValidationError() {
        AssetFilterRequest invalidDateRequest = new AssetFilterRequest(
                "test.jpg",
                "image/jpeg",
                "2024-12-31T00:00:00",
                "2024-01-01T00:00:00",
                "ASC"
        );

        when(assetValidator.validateSortDirection("ASC")).thenReturn(Mono.empty());
        when(assetValidator.validateDateRange(
                LocalDateTime.parse("2024-12-31T00:00:00"),
                LocalDateTime.parse("2024-01-01T00:00:00")))
                .thenReturn(Mono.error(new InvalidDateRangeException("Invalid date range")));

        StepVerifier.create(getAssetsService.find(invalidDateRequest))
                .expectError(InvalidDateRangeException.class)
                .verify();

        verify(assetValidator).validateSortDirection("ASC");
        verify(assetValidator).validateDateRange(
                LocalDateTime.parse("2024-12-31T00:00:00"),
                LocalDateTime.parse("2024-01-01T00:00:00"));
        verify(getAssetsByFilterUseCase, never()).find(any());
    }


    @Test
    void find_shouldHandleEmptyResult() {
        when(assetMapper.toCommand(request)).thenReturn(command);
        when(assetValidator.validateSortDirection("ASC")).thenReturn(Mono.empty());
        when(assetValidator.validateDateRange(command.uploadDateStart(), command.uploadDateEnd())).thenReturn(Mono.empty());
        when(getAssetsByFilterUseCase.find(command)).thenReturn(Flux.empty());

        StepVerifier.create(getAssetsService.find(request))
                .expectNextCount(0)
                .verifyComplete();
    }
}