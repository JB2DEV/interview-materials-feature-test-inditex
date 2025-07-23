package com.interview.materials.feature.test.inditex.infraestructure.adapter.out.repository;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.port.out.repository.AssetRepositoryPort;
import com.interview.materials.feature.test.inditex.infraestructure.db.entity.AssetEntity;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.infraestructure.adapter.out.repository.query.AssetQueryBuilder;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class AssetRepositoryAdapter implements AssetRepositoryPort {

    private final R2dbcEntityTemplate template;
    private final AssetMapper assetMapper;

    @Override
    public Mono<Asset> save(Asset asset) {
        AssetEntity entity = assetMapper.toPersistence(asset);
        return TraceIdHolder.getTraceId()
                .doOnNext(traceId ->
                        log.info("[traceId={}] Saving asset with filename='{}'", traceId, entity.getFilename()))
                .then(template.insert(AssetEntity.class)
                        .using(entity)
                        .map(assetMapper::toDomain));
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
                    .map(assetMapper::toDomain);
        });
    }
}