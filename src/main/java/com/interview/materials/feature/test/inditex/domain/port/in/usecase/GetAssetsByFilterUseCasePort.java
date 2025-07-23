package com.interview.materials.feature.test.inditex.domain.port.in.usecase;

import com.interview.materials.feature.test.inditex.application.command.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import reactor.core.publisher.Flux;

public interface GetAssetsByFilterUseCasePort {
    Flux<Asset> find(FindAssetsByFiltersCommand command);
}
