package com.interview.materials.feature.test.inditex.application.usecase;

import com.interview.materials.feature.test.inditex.application.command.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.port.out.repository.AssetRepositoryPort;
import com.interview.materials.feature.test.inditex.domain.usecase.GetAssetsByFilterUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetAssetsByFilterUseCaseImpl implements GetAssetsByFilterUseCase {

    private final AssetRepositoryPort assetRepository;

    @Override
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