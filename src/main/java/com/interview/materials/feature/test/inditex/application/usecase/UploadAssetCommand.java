package com.interview.materials.feature.test.inditex.application.usecase;

import com.interview.materials.feature.test.inditex.infraestructure.web.error.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class UploadAssetCommand extends SelfValidating<UploadAssetCommand> {

    @NotBlank(message = "filename cannot be blank")
    private final String filename;

    @NotBlank(message = "contentType cannot be blank")
    private final String contentType;

    @NotBlank(message = "encodedFile cannot be blank")
    private final String encodedFile;

    @NotNull(message = "size must be provided")
    private final Long size;

    @Builder
    public UploadAssetCommand(String filename, String contentType, String encodedFile, Long size) {
        this.filename = filename;
        this.contentType = contentType;
        this.encodedFile = encodedFile;
        this.size = size;
        this.validateSelf();
    }
}
