package com.interview.materials.feature.test.inditex.infraestructure.db.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AssetEntity {

    @Id
    @Setter(AccessLevel.NONE)
    private UUID id;

    private String filename;

    @Column("content_type")
    private String contentType;

    private String url;

    private Long size;

    @CreatedDate
    @Column("upload_date")
    private LocalDateTime uploadDate;
}