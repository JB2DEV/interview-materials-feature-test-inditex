package com.interview.materials.feature.test.inditex.infraestructure.config;

import com.interview.materials.feature.test.inditex.application.usecase.UploadAssetUseCase;
import com.interview.materials.feature.test.inditex.domain.port.in.usecase.UploadAssetUseCasePort;
import com.interview.materials.feature.test.inditex.infraestructure.adapter.in.service.UploadAssetServiceAdapter;
import com.interview.materials.feature.test.inditex.application.port.in.service.UploadAssetServicePort;
import com.interview.materials.feature.test.inditex.application.service.GetAssetsServiceImpl;
import com.interview.materials.feature.test.inditex.application.usecase.GetAssetsByFilterUseCaseImpl;
import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.domain.port.out.repository.AssetRepositoryPort;
import com.interview.materials.feature.test.inditex.domain.service.GetAssetsService;
import com.interview.materials.feature.test.inditex.domain.usecase.GetAssetsByFilterUseCase;
import com.interview.materials.feature.test.inditex.infraestructure.adapter.out.repository.AssetRepositoryAdapter;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
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

    //TODO Eliminar toda config de infra, ya que es implementación propia. Lo demás dejarlo con beans
    @Bean
    public AssetRepositoryPort assetRepositoryPort() {
        return new AssetRepositoryAdapter(r2dbcEntityTemplate, assetMapper);
    }

    @Bean
    public UploadAssetUseCasePort uploadAssetUseCase(AssetRepositoryPort assetRepositoryPort) {
        return new UploadAssetUseCase(assetRepositoryPort);
    }

    @Bean
    public UploadAssetServicePort uploadAssetServicePort(
            UploadAssetUseCasePort uploadAssetUseCasePort,
            AssetValidator        assetValidator,
            AssetMapper           assetMapper
    ) {
        return new UploadAssetServiceAdapter(
                uploadAssetUseCasePort,
                assetValidator,
                assetMapper
        );
    }

    @Bean
    public GetAssetsService getAssetsService() {
        return new GetAssetsServiceImpl(getAssetsByFilterUseCase(), assetValidator, assetMapper);
    }

    @Bean
    public GetAssetsByFilterUseCase getAssetsByFilterUseCase() {
        return new GetAssetsByFilterUseCaseImpl(assetRepositoryPort());
    }
}