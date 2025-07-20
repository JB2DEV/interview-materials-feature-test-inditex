package com.interview.materials.feature.test.inditex.application.service;

import com.interview.materials.feature.test.inditex.application.usecase.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.application.usecase.GetAssetsByFilterUseCase;
import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFilterRequest;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAssetsService {

    private final GetAssetsByFilterUseCase getAssetsByFilterUseCase;
    private final AssetValidator assetValidator;
    private final AssetMapper assetMapper;

    public Flux<Asset> find(AssetFilterRequest requestDto) {
        //TODO Fix that. Unit Test are failing because of this
        FindAssetsByFiltersCommand command = assetMapper.toCommand(requestDto);

        return Mono.when(
                        assetValidator.validateSortDirection(String.valueOf(command.sortDirection())),
                        assetValidator.validateDateRange(command.uploadDateStart(), command.uploadDateEnd())
                )
                .then(TraceIdHolder.getTraceId())
                .doOnNext(traceId -> log.info("[traceId={}] Handling asset search use case", traceId))
                .thenMany(getAssetsByFilterUseCase.find(command));
    }
}
