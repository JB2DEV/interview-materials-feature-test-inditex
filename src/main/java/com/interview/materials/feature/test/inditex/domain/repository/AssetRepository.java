package com.interview.materials.feature.test.inditex.domain.repository;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface AssetRepository {

    Mono<Asset> save(Asset asset);

    Flux<Asset> findByFilters(
            String filename,
            String fileType,
            LocalDateTime uploadDateStart,
            LocalDateTime uploadDateEnd,
            String sortDirection
    );
}
