package com.interview.materials.feature.test.inditex.application.service;

import com.interview.materials.feature.test.inditex.application.usecase.UploadAssetCommand;
import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.service.UploadAssetService;
import com.interview.materials.feature.test.inditex.domain.usecase.UploadAssetUseCase;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadRequest;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadResponse;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadAssetServiceImpl implements UploadAssetService {

    private final UploadAssetUseCase uploadAssetUseCase;
    private final AssetValidator assetValidator;
    private final AssetMapper assetMapper;

    public Mono<AssetFileUploadResponse> handle(AssetFileUploadRequest requestDto) {
        return Mono.when(
                        assetValidator.validateEncodedFile(requestDto.encodedFile()),
                        assetValidator.validateContentType(requestDto.contentType())
                )
                .then(Mono.defer(() -> {
                    UploadAssetCommand command = assetMapper.toCommand(requestDto);
                    Asset domainAsset = assetMapper.toDomain(command, generateFinalUrl(requestDto.filename()));

                    return TraceIdHolder.getTraceId()
                            .doOnNext(traceId -> log.info("[traceId={}] Handling asset upload use case", traceId))
                            .then(uploadAssetUseCase.upload(domainAsset))
                            .map(assetMapper::toResponse);
                }));
    }

    private String generateFinalUrl(String filename) {
        // Fake URL generation logic for demonstration purposes
        return "https://assets.cdn.fake/" + filename;
    }
}