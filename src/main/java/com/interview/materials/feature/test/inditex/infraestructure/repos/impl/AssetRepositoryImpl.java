package com.interview.materials.feature.test.inditex.infraestructure.repos.impl;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.model.AssetId;
import com.interview.materials.feature.test.inditex.domain.repository.AssetRepository;
import com.interview.materials.feature.test.inditex.infraestructure.db.entity.AssetEntity;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.infraestructure.repos.impl.query.AssetQueryBuilder;
import com.interview.materials.feature.test.inditex.infraestructure.repos.r2dbc.AssetEntityRepositoryR2dbc;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
public class AssetRepositoryImpl implements AssetRepository {

    private final R2dbcEntityTemplate template;
    private final AssetEntityRepositoryR2dbc reader;

    @Override
    public Mono<Asset> save(Asset asset) {
        AssetEntity entity = AssetMapper.toPersistence(asset);
        return TraceIdHolder.getTraceId()
                .doOnNext(traceId ->
                        log.info("[traceId={}] Saving asset with filename='{}'", traceId, entity.getFilename()))
                .then(template.insert(AssetEntity.class)
                        .using(entity)
                        .map(AssetMapper::toDomain));
    }

    @Override
    public Flux<Asset> findByFilters(String filename, String contentType, LocalDateTime uploadDateStart, LocalDateTime uploadDateEnd, String sortDirection) {
        return TraceIdHolder.getTraceId().flatMapMany(traceId -> {
            log.info("[traceId={}] Filtering assets with filename='{}', fileType='{}', uploadDateStart='{}', uploadDateEnd='{}', sortDirection='{}'",
                    traceId, filename, contentType, uploadDateStart, uploadDateEnd, sortDirection);

            AssetQueryBuilder queryBuilder = new AssetQueryBuilder()
                    .withFilename(filename)
                    .withContentType(contentType)
                    .withUploadDateStart(uploadDateStart)
                    .withUploadDateEnd(uploadDateEnd)
                    .withSort(sortDirection);

            return queryBuilder.bindTo(template.getDatabaseClient())
                    .map((row, meta) -> AssetEntity.builder()
                            .id(row.get("id", UUID.class))
                            .filename(row.get("filename", String.class))
                            .contentType(row.get("content_type", String.class))
                            .url(row.get("url", String.class))
                            .size(row.get("size", Long.class))
                            .uploadDate(row.get("upload_date", LocalDateTime.class))
                            .build())
                    .all()
                    .map(AssetMapper::toDomain);
        });
    }

    @Override
    public Mono<Asset> findById(AssetId id) {
        //TODO : Implement find by ID logic
        return null;
    }
}