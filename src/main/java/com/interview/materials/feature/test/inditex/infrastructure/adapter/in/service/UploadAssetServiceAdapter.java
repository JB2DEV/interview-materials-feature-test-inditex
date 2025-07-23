package com.interview.materials.feature.test.inditex.infrastructure.adapter.in.service;

import com.interview.materials.feature.test.inditex.application.command.UploadAssetCommand;
import com.interview.materials.feature.test.inditex.application.port.in.service.UploadAssetServicePort;
import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.port.in.usecase.UploadAssetUseCasePort;
import com.interview.materials.feature.test.inditex.infrastructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class UploadAssetServiceAdapter implements UploadAssetServicePort {

    private final UploadAssetUseCasePort uploadAssetUseCasePort;
    private final AssetValidator assetValidator;
    private final AssetMapper assetMapper;

    public Mono<Asset> handle(UploadAssetCommand command) {
        return Mono.when(
                        assetValidator.validateContentType(command.contentType())
                )
                .then(Mono.defer(() -> {
                    Asset domainAsset = assetMapper.toDomain(command,generateFinalUrl(command.filename()));
                    return TraceIdHolder.getTraceId()
                            .doOnNext(traceId -> log.info("[traceId={}] Handling asset upload use case", traceId))
                            .then(uploadAssetUseCasePort.upload(domainAsset));
                }));
    }

    private String generateFinalUrl(String filename) {
        // Fake URL generation logic for demonstration purposes
        return "https://assets.cdn.fake/" + filename;
    }
}