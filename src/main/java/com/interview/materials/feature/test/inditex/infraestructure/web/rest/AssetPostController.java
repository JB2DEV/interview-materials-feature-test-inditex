package com.interview.materials.feature.test.inditex.infraestructure.web.rest;

import com.interview.materials.feature.test.inditex.application.service.UploadAssetService;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadRequest;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mgmt/1/assets/actions")
@RequiredArgsConstructor
public class AssetPostController {

    private final UploadAssetService uploadAssetService;

    @PostMapping("/upload")
    public Mono<ResponseEntity<AssetFileUploadResponse>> uploadAsset(@RequestBody AssetFileUploadRequest request) {
        return uploadAssetService.handle(request)
                .map(response -> ResponseEntity.accepted().body(response));
    }
}