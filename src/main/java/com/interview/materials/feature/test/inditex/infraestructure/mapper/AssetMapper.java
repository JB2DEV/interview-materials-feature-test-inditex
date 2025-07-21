package com.interview.materials.feature.test.inditex.infraestructure.mapper;

import com.interview.materials.feature.test.inditex.application.usecase.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.application.usecase.UploadAssetCommand;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.model.AssetId;
import com.interview.materials.feature.test.inditex.infraestructure.db.entity.AssetEntity;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadRequest;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadResponse;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFilterRequest;
import com.interview.materials.feature.test.inditex.shared.enums.SortDirection;
import com.interview.materials.feature.test.inditex.shared.utils.DateParser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Component
public class AssetMapper {

    // Domain to Persistence
    public AssetEntity toPersistence(Asset asset) {
        return AssetEntity.builder()
                .id(asset.getId().getValue())
                .filename(asset.getFilename())
                .contentType(asset.getContentType())
                .url(asset.getUrl())
                .size(asset.getSize())
                .uploadDate(asset.getUploadDate())
                .build();
    }

    // Persistence to Domain
    public Asset toDomain(AssetEntity entity) {
        return Asset.builder()
                .id(AssetId.of(entity.getId()))
                .filename(entity.getFilename())
                .contentType(entity.getContentType())
                .url(entity.getUrl())
                .size(entity.getSize())
                .uploadDate(entity.getUploadDate())
                .build();
    }

    // DTO to Command
    public UploadAssetCommand toCommand(AssetFileUploadRequest dto) {
        long size = Base64.getDecoder().decode(dto.encodedFile()).length;

        return UploadAssetCommand.builder()
                .filename(dto.filename())
                .contentType(dto.contentType())
                .encodedFile(dto.encodedFile())
                .size(size)
                .build();
    }

    // DTO to Command
    public FindAssetsByFiltersCommand toCommand(AssetFilterRequest request) {
        return FindAssetsByFiltersCommand.builder()
                .filename(request.filename())
                .contentType(request.filetype())
                .uploadDateStart(DateParser.parse(request.uploadDateStart()))
                .uploadDateEnd(DateParser.parse(request.uploadDateEnd()))
                .sortDirection(SortDirection.from(request.sortDirection()))
                .build();
    }

    // Command + URL to Domain
    public Asset toDomain(UploadAssetCommand command, String uploadedUrl) {
        return Asset.builder()
                .id(AssetId.of(UUID.randomUUID()))
                .filename(command.filename())
                .contentType(command.contentType())
                .url(uploadedUrl)
                .size(command.size())
                .uploadDate(LocalDateTime.now())
                .build();
    }

    // Domain to DTO
    public AssetFileUploadResponse toResponse(Asset asset) {
        return new AssetFileUploadResponse(asset.getId().getValue().toString());
    }
}