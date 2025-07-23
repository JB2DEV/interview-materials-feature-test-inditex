package com.interview.materials.feature.test.inditex.infrastructure.web.dto;

public record AssetFilterRequest(
        String filename,
        String filetype,
        String uploadDateStart,
        String uploadDateEnd,
        String sortDirection
) {
}