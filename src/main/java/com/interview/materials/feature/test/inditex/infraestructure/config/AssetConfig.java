package com.interview.materials.feature.test.inditex.infraestructure.config;

import com.interview.materials.feature.test.inditex.application.usecase.UploadAssetUseCase;
import com.interview.materials.feature.test.inditex.domain.repository.AssetRepository;
import com.interview.materials.feature.test.inditex.infraestructure.repos.impl.AssetRepositoryImpl;
import com.interview.materials.feature.test.inditex.infraestructure.repos.r2dbc.AssetEntityRepositoryR2dbc;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@Configuration
@RequiredArgsConstructor
public class AssetConfig {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final AssetEntityRepositoryR2dbc assetEntityRepositoryR2dbc;

    @Bean
    public AssetRepository assetRepositoryR2dbc() {
        return new AssetRepositoryImpl(r2dbcEntityTemplate, assetEntityRepositoryR2dbc);
    }

    @Bean
    public UploadAssetUseCase uploadAssetUseCase() {
        return new UploadAssetUseCase(assetRepositoryR2dbc());
    }
}