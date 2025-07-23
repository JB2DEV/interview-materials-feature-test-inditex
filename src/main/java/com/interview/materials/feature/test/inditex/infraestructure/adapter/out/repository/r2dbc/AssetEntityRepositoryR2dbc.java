package com.interview.materials.feature.test.inditex.infraestructure.adapter.out.repository.r2dbc;

import com.interview.materials.feature.test.inditex.infraestructure.db.entity.AssetEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface AssetEntityRepositoryR2dbc extends ReactiveCrudRepository<AssetEntity, UUID> {
}