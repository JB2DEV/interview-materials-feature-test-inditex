package com.interview.materials.feature.test.inditex.application.service;

import com.interview.materials.feature.test.inditex.application.usecase.UploadAssetCommand;
import com.interview.materials.feature.test.inditex.application.usecase.UploadAssetUseCase;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadRequest;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UploadAssetService {

    private final UploadAssetUseCase uploadAssetUseCase;

    public Mono<AssetFileUploadResponse> handle(AssetFileUploadRequest requestDto) {
        UploadAssetCommand command = AssetMapper.toCommand(requestDto);
        Asset domainAsset = AssetMapper.toDomain(command, generateFinalUrl(requestDto.filename()));

        return uploadAssetUseCase.upload(domainAsset)
                .map(AssetMapper::toResponse);
    }

    private String generateFinalUrl(String filename) {
        // Fake URL generation logic for demonstration purposes
        return "https://assets.cdn.fake/" + filename;
    }
}