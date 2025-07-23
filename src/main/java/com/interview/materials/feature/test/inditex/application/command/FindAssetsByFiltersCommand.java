package com.interview.materials.feature.test.inditex.application.command;

import com.interview.materials.feature.test.inditex.shared.enums.SortDirection;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Accessors(fluent = true)
public class FindAssetsByFiltersCommand {

    private final String filename;
    private final String contentType;
    private final LocalDateTime uploadDateStart;
    private final LocalDateTime uploadDateEnd;
    private final SortDirection sortDirection;

    @Builder
    public FindAssetsByFiltersCommand(String filename, String contentType, LocalDateTime uploadDateStart,
                                      LocalDateTime uploadDateEnd, SortDirection sortDirection) {
        this.filename = filename;
        this.contentType = contentType;
        this.uploadDateStart = uploadDateStart;
        this.uploadDateEnd = uploadDateEnd;
        this.sortDirection = sortDirection;
    }
}
