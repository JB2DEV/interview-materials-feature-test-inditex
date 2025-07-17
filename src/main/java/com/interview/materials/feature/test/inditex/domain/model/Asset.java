package com.interview.materials.feature.test.inditex.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("assets")
public class Asset {

    @Id
    private AssetId id;

    private String filename;

    @Column("content_type")
    private String contentType;

    private String url;

    private long size;

    @Column("upload_date")
    private LocalDateTime uploadDate;
}
