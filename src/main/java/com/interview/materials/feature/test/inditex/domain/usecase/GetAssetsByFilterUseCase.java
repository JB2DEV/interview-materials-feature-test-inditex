package com.interview.materials.feature.test.inditex.domain.usecase;

import com.interview.materials.feature.test.inditex.application.usecase.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import reactor.core.publisher.Flux;

public interface GetAssetsByFilterUseCase {
    Flux<Asset> find(FindAssetsByFiltersCommand command);
}
