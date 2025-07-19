package com.interview.materials.feature.test.inditex.infraestructure.repos.impl.query;

import lombok.Getter;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.interview.materials.feature.test.inditex.infraestructure.db.consts.AssetTable.*;
import static com.interview.materials.feature.test.inditex.infraestructure.db.consts.SQL.AND;
import static com.interview.materials.feature.test.inditex.infraestructure.db.consts.SQL.ORDER_BY;

@Getter
public class AssetQueryBuilder {

    private final StringBuilder sql;
    private final Map<String, Object> bindings;

    public AssetQueryBuilder() {
        this.sql = new StringBuilder("SELECT * FROM ").append(TABLE_NAME).append(" WHERE 1=1");
        this.bindings = new HashMap<>();
    }

    public AssetQueryBuilder withFilename(String filename) {
        if (StringUtils.hasText(filename)) {
            sql.append(AND).append(COL_FILENAME).append(" ILIKE :filename");
            bindings.put("filename", "%" + filename + "%");
        }
        return this;
    }

    public AssetQueryBuilder withContentType(String contentType) {
        if (StringUtils.hasText(contentType)) {
            sql.append(AND).append(COL_CONTENT_TYPE).append(" = :contentType");
            bindings.put("contentType", contentType);
        }
        return this;
    }

    public AssetQueryBuilder withUploadDateStart(LocalDateTime start) {
        if (start != null) {
            sql.append(AND).append(COL_UPLOAD_DATE).append(" >= :uploadDateStart");
            bindings.put("uploadDateStart", start);
        }
        return this;
    }

    public AssetQueryBuilder withUploadDateEnd(LocalDateTime end) {
        if (end != null) {
            sql.append(AND).append(COL_UPLOAD_DATE).append(" <= :uploadDateEnd");
            bindings.put("uploadDateEnd", end);
        }
        return this;
    }

    public AssetQueryBuilder withSort(String sortDirection) {
        String direction = "DESC".equalsIgnoreCase(sortDirection) ? "DESC" : "ASC";
        sql.append(ORDER_BY).append(COL_UPLOAD_DATE).append(" ").append(direction);
        return this;
    }

    public DatabaseClient.GenericExecuteSpec bindTo(DatabaseClient client) {
        DatabaseClient.GenericExecuteSpec spec = client.sql(sql.toString());
        for (Map.Entry<String, Object> entry : bindings.entrySet()) {
            spec = spec.bind(entry.getKey(), entry.getValue());
        }
        return spec;
    }
}