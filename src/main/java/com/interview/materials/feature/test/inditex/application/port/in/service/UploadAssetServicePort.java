package com.interview.materials.feature.test.inditex.application.port.in.service;

import com.interview.materials.feature.test.inditex.application.command.UploadAssetCommand;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import reactor.core.publisher.Mono;

public interface UploadAssetServicePort {
    Mono<Asset> handle(UploadAssetCommand uploadAssetCommand);
}