package com.interview.materials.feature.test.inditex.infraestructure.repos.impl;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.model.AssetId;
import com.interview.materials.feature.test.inditex.domain.repository.AssetRepository;
import com.interview.materials.feature.test.inditex.infraestructure.db.entity.AssetEntity;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.infraestructure.repos.r2dbc.AssetEntityRepositoryR2dbc;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@RequiredArgsConstructor
public class AssetRepositoryImpl implements AssetRepository {

    private final R2dbcEntityTemplate template;
    private final AssetEntityRepositoryR2dbc reader;

    @Override
    public Mono<Asset> save(Asset asset) {
        AssetEntity entity = AssetMapper.toPersistence(asset);
        return template.insert(AssetEntity.class)
                .using(entity)
                .map(AssetMapper::toDomain);
    }

    @Override
    public Flux<Asset> findByFilters(String filename, String fileType, LocalDateTime uploadDateStart, LocalDateTime uploadDateEnd, String sortDirection) {
        // TODO: Implement filtering logic based on the provided parameters
        return null;
    }

    @Override
    public Mono<Asset> findById(AssetId id) {
        //TODO : Implement find by ID logic
        return null;
    }
}