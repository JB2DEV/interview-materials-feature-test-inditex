package com.interview.materials.feature.test.inditex.application.service;

import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.service.GetAssetsService;
import com.interview.materials.feature.test.inditex.domain.usecase.GetAssetsByFilterUseCase;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFilterRequest;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import com.interview.materials.feature.test.inditex.shared.utils.DateParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAssetsServiceImpl implements GetAssetsService {

    private final GetAssetsByFilterUseCase getAssetsByFilterUseCase;
    private final AssetValidator assetValidator;
    private final AssetMapper assetMapper;

    @Override
    public Flux<Asset> find(AssetFilterRequest requestDto) {
        Mono<Void> sortValidation = assetValidator.validateSortDirection(requestDto.sortDirection());
        Mono<Void> dateRangeValidation = (requestDto.uploadDateStart() != null && requestDto.uploadDateEnd() != null)
                ? assetValidator.validateDateRange(DateParser.parse(requestDto.uploadDateStart()), DateParser.parse(requestDto.uploadDateEnd()))
                : Mono.empty();
        return Mono.when(sortValidation, dateRangeValidation)
                .then(Mono.fromCallable(() -> assetMapper.toCommand(requestDto)))
                .flatMapMany(command ->
                        TraceIdHolder.getTraceId()
                                .doOnNext(traceId -> log.info("[traceId={}] Handling asset search use case", traceId))
                                .thenMany(getAssetsByFilterUseCase.find(command))
                );
    }
}
