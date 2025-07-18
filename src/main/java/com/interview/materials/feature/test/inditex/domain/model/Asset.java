package com.interview.materials.feature.test.inditex.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Setter(AccessLevel.NONE)
    private AssetId id;

    private String filename;

    private String contentType;

    private String url;

    private long size;

    private LocalDateTime uploadDate;
}
