package com.interview.materials.feature.test.inditex.domain.port.in.usecase;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import reactor.core.publisher.Mono;

public interface UploadAssetUseCasePort {
    Mono<Asset> upload(Asset asset);
}