package com.interview.materials.feature.test.inditex.appication.service;

import com.interview.materials.feature.test.inditex.application.service.GetAssetsService;
import com.interview.materials.feature.test.inditex.application.usecase.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.application.usecase.GetAssetsByFilterUseCase;
import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidSortDirectionException;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.model.AssetId;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFilterRequest;
import com.interview.materials.feature.test.inditex.shared.enums.SortDirection;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
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
    }

    @Test
    void shouldReturnAssetsWhenRequestIsValid() {
        Asset asset = new Asset(
                AssetId.of(UUID.randomUUID()),
                "test.jpg",
                "image/jpeg",
                "https://cdn.fake/test.jpg",
                1024L,
                LocalDateTime.now()
        );

        // mocks
        when(assetMapper.toCommand(request)).thenReturn(command);
        when(assetValidator.validateSortDirection("ASC")).thenReturn(Mono.empty());
        when(assetValidator.validateDateRange(command.uploadDateStart(), command.uploadDateEnd())).thenReturn(Mono.empty());
        when(getAssetsByFilterUseCase.find(command)).thenReturn(Flux.just(asset));

        // execute
        StepVerifier.create(getAssetsService.find(request))
                .expectNext(asset)
                .verifyComplete();

        // verify
        verify(assetMapper).toCommand(request);
        verify(assetValidator).validateSortDirection("ASC");
        verify(assetValidator).validateDateRange(command.uploadDateStart(), command.uploadDateEnd());
        verify(getAssetsByFilterUseCase).find(command);
    }

    @Test
    void shouldFailWhenSortDirectionIsInvalid() {
        AssetFilterRequest invalidRequest = new AssetFilterRequest(
                "file.jpg", "image/jpeg", "2024-01-01T00:00:00", "2024-12-31T00:00:00", "ERROR"
        );

        // The mapper will throw the exception when converting
        when(assetMapper.toCommand(invalidRequest))
                .thenThrow(new InvalidSortDirectionException("Invalid sort direction"));

        // execute and verify
        StepVerifier.create(getAssetsService.find(invalidRequest))
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidSortDirectionException &&
                                throwable.getMessage().equals("Invalid sort direction"))
                .verify();

        verify(assetMapper).toCommand(invalidRequest);
        verifyNoInteractions(assetValidator, getAssetsByFilterUseCase);
    }
}
