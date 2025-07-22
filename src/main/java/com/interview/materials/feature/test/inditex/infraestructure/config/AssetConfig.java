package com.interview.materials.feature.test.inditex.infraestructure.config;

import com.interview.materials.feature.test.inditex.application.service.GetAssetsServiceImpl;
import com.interview.materials.feature.test.inditex.application.service.UploadAssetServiceImpl;
import com.interview.materials.feature.test.inditex.application.usecase.GetAssetsByFilterUseCaseImpl;
import com.interview.materials.feature.test.inditex.application.usecase.UploadAssetUseCaseImpl;
import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.domain.repository.AssetRepository;
import com.interview.materials.feature.test.inditex.domain.service.GetAssetsService;
import com.interview.materials.feature.test.inditex.domain.service.UploadAssetService;
import com.interview.materials.feature.test.inditex.domain.usecase.GetAssetsByFilterUseCase;
import com.interview.materials.feature.test.inditex.domain.usecase.UploadAssetUseCase;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.infraestructure.repos.impl.AssetRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@Configuration
@RequiredArgsConstructor
public class AssetConfig {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final AssetValidator assetValidator;
    private final AssetMapper assetMapper;

    @Bean
    public AssetRepository assetRepositoryR2dbc() {
        return new AssetRepositoryImpl(r2dbcEntityTemplate,assetMapper);
    }

    @Bean
    public UploadAssetService uploadAssetService() {
        return new UploadAssetServiceImpl(uploadAssetUseCase(), assetValidator, assetMapper);
    }

    @Bean
    public GetAssetsService getAssetsService() {
        return new GetAssetsServiceImpl(getAssetsByFilterUseCase(), assetValidator, assetMapper);
    }

    @Bean
    public UploadAssetUseCase uploadAssetUseCase() {
        return new UploadAssetUseCaseImpl(assetRepositoryR2dbc());
    }

    @Bean
    public GetAssetsByFilterUseCase getAssetsByFilterUseCase() {
        return new GetAssetsByFilterUseCaseImpl(assetRepositoryR2dbc());
    }
}