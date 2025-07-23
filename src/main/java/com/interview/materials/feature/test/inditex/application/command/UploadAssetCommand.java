package com.interview.materials.feature.test.inditex.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class UploadAssetCommand {

    private final String filename;
    private final String contentType;
    private final String encodedFile;
    private final Long size;

    @Builder
    public UploadAssetCommand(String filename, String contentType, String encodedFile, Long size) {
        this.filename = filename;
        this.contentType = contentType;
        this.encodedFile = encodedFile;
        this.size = size;
    }
}