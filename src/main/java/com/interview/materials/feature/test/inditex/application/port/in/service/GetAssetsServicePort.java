package com.interview.materials.feature.test.inditex.application.port.in.service;

import com.interview.materials.feature.test.inditex.application.command.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import reactor.core.publisher.Flux;

public interface GetAssetsServicePort {
    Flux<Asset> find(FindAssetsByFiltersCommand command);
}