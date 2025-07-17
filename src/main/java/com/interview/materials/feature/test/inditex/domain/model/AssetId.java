package com.interview.materials.feature.test.inditex.domain.model;

import lombok.Value;

import java.util.UUID;

@Value
public class AssetId {

    UUID value;

    public static AssetId of(UUID value) {
        return new AssetId(value);
    }
}
