package com.interview.materials.feature.test.inditex.infrastructure.config;

import com.interview.materials.feature.test.inditex.application.usecase.GetAssetsByFilterUseCase;
import com.interview.materials.feature.test.inditex.application.usecase.UploadAssetUseCase;
import com.interview.materials.feature.test.inditex.domain.port.in.usecase.GetAssetsByFilterUseCasePort;
import com.interview.materials.feature.test.inditex.domain.port.in.usecase.UploadAssetUseCasePort;
import com.interview.materials.feature.test.inditex.domain.port.out.repository.AssetRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AssetConfig {

    @Bean
    public UploadAssetUseCasePort uploadAssetUseCase(AssetRepositoryPort assetRepositoryPort) {
        return new UploadAssetUseCase(assetRepositoryPort);
    }

    @Bean
    public GetAssetsByFilterUseCasePort getAssetsByFilterUseCase(
            AssetRepositoryPort assetRepositoryPort
    ) {
        return new GetAssetsByFilterUseCase(assetRepositoryPort);
    }
}