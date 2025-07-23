package com.interview.materials.feature.test.inditex.infraestructure.adapter.in.service;

import com.interview.materials.feature.test.inditex.application.command.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.application.port.in.service.GetAssetsServicePort;
import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.port.in.usecase.GetAssetsByFilterUseCasePort;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAssetsServiceAdapter implements GetAssetsServicePort {

    private final GetAssetsByFilterUseCasePort getAssetsByFilterUseCasePort;
    private final AssetValidator assetValidator;

    @Override
    public Flux<Asset> find(FindAssetsByFiltersCommand command) {
        Mono<Void> sortValidation = assetValidator.validateSortDirection(String.valueOf(command.sortDirection()));
        Mono<Void> dateRangeValidation = Mono.empty();
        if (command.uploadDateStart() != null && command.uploadDateEnd() != null) {
            dateRangeValidation = assetValidator.validateDateRange(command.uploadDateStart(), command.uploadDateEnd());
        }
        return Mono.when(sortValidation, dateRangeValidation)
                .thenMany(
                        TraceIdHolder.getTraceId()
                                .doOnNext(traceId -> log.info("[traceId={}] Handling asset search use case", traceId))
                                .thenMany(getAssetsByFilterUseCasePort.find(command))
                );
    }
}
