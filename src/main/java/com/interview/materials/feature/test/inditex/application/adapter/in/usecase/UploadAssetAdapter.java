package com.interview.materials.feature.test.inditex.application.adapter.in.usecase;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.port.in.usecase.UploadAssetUseCase;
import com.interview.materials.feature.test.inditex.domain.port.out.repository.AssetRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UploadAssetAdapter implements UploadAssetUseCase {

    private final AssetRepositoryPort assetRepository;

    @Override
    public Mono<Asset> upload(Asset asset) {
        return assetRepository.save(asset);
    }
}