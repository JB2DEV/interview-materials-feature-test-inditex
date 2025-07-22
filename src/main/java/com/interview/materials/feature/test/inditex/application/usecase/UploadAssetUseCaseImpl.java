package com.interview.materials.feature.test.inditex.application.usecase;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.repository.AssetRepository;
import com.interview.materials.feature.test.inditex.domain.usecase.UploadAssetUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UploadAssetUseCaseImpl implements UploadAssetUseCase {

    private final AssetRepository assetRepository;

    @Override
    public Mono<Asset> upload(Asset asset) {
        return assetRepository.save(asset);
    }
}