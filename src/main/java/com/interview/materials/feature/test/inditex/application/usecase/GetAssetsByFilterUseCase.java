package com.interview.materials.feature.test.inditex.application.usecase;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetAssetsByFilterUseCase {

    private final AssetRepository assetRepository;

    public Flux<Asset> find(FindAssetsByFiltersCommand command) {
        return assetRepository.findByFilters(
                command.filename(),
                command.contentType(),
                command.uploadDateStart(),
                command.uploadDateEnd(),
                String.valueOf(command.sortDirection())
        );
    }
}