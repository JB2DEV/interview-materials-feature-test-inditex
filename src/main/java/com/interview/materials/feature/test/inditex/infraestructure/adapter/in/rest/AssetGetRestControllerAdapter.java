package com.interview.materials.feature.test.inditex.infraestructure.adapter.in.rest;

import com.interview.materials.feature.test.inditex.application.command.FindAssetsByFiltersCommand;
import com.interview.materials.feature.test.inditex.application.port.in.service.GetAssetsServicePort;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
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
public class AssetGetRestControllerAdapter {

    private final GetAssetsServicePort getAssetsServicePort;
    private final AssetMapper assetMapper;

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
        FindAssetsByFiltersCommand command = assetMapper.toCommand(request);
        return TraceIdHolder.getTraceId()
                .doOnNext(traceId -> log.info("[traceId={}] Incoming request to GET /assets", traceId))
                .thenMany(getAssetsServicePort.find(command));
    }
}