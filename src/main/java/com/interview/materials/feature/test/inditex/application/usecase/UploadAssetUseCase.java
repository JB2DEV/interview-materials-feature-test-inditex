package com.interview.materials.feature.test.inditex.application.usecase;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.port.in.usecase.UploadAssetUseCasePort;
import com.interview.materials.feature.test.inditex.domain.port.out.repository.AssetRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UploadAssetUseCase implements UploadAssetUseCasePort {

    private final AssetRepositoryPort assetRepository;

    @Override
    public Mono<Asset> upload(Asset asset) {
        return assetRepository.save(asset);
    }
}