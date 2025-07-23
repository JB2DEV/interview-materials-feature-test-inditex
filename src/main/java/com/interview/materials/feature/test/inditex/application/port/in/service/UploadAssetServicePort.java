package com.interview.materials.feature.test.inditex.application.port.in.service;

import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadRequest;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadResponse;
import reactor.core.publisher.Mono;

public interface UploadAssetServicePort {
    Mono<AssetFileUploadResponse> handle(AssetFileUploadRequest requestDto);
}