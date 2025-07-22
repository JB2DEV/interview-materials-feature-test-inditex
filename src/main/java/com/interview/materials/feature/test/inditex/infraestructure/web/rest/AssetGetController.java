package com.interview.materials.feature.test.inditex.infraestructure.web.rest;

import com.interview.materials.feature.test.inditex.application.service.GetAssetsServiceImpl;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.service.GetAssetsService;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFilterRequest;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/mgmt/1/assets")
@RequiredArgsConstructor
@Slf4j
public class AssetGetController {

    private final GetAssetsService getAssetsService;

    @GetMapping
    public Flux<Asset> getAssetsByFilters(
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) String filetype,
            @RequestParam(required = false) String uploadDateStart,
            @RequestParam(required = false) String uploadDateEnd,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        AssetFilterRequest request = new AssetFilterRequest(
                filename, filetype, uploadDateStart, uploadDateEnd, sortDirection
        );
        return TraceIdHolder.getTraceId()
                .doOnNext(traceId -> log.info("[traceId={}] Incoming request to GET /assets", traceId))
                .thenMany(getAssetsService.find(request));
    }
}