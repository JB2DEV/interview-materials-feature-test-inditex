package com.interview.materials.feature.test.inditex.domain.service;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFilterRequest;
import reactor.core.publisher.Flux;

public interface GetAssetsService {
    Flux<Asset> find(AssetFilterRequest requestDto);
}