package com.interview.materials.feature.test.inditex.infraestructure.web.rest;

import com.interview.materials.feature.test.inditex.domain.service.UploadAssetService;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadRequest;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadResponse;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mgmt/1/assets/actions")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AssetPostController {

    private final UploadAssetService uploadAssetService;

    @PostMapping("/upload")
    public Mono<ResponseEntity<AssetFileUploadResponse>> uploadAsset(@Valid @RequestBody AssetFileUploadRequest request) {
        return TraceIdHolder.getTraceId()
                .doOnNext(traceId -> log.info("[traceId={}] Upload request received", traceId))
                .then(uploadAssetService.handle(request))
                .map(response -> ResponseEntity.accepted().body(response));
    }
}