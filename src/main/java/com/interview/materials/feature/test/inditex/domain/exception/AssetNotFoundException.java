package com.interview.materials.feature.test.inditex.domain.exception;

import com.interview.materials.feature.test.inditex.domain.model.AssetId;

public class AssetNotFoundException extends DomainEntityNotFoundException {

    public AssetNotFoundException() {
        super("task");
    }

    public AssetNotFoundException(AssetId id) {
        super("task", id.getValue().toString());
    }
}