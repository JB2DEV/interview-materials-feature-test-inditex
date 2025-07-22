package com.interview.materials.feature.test.inditex.domain.service;

import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadRequest;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadResponse;
import reactor.core.publisher.Mono;

public interface UploadAssetService {
    Mono<AssetFileUploadResponse> handle(AssetFileUploadRequest requestDto);
}