package com.interview.materials.feature.test.inditex.application.service;

import com.interview.materials.feature.test.inditex.application.adapter.in.service.UploadAssetServiceAdapter;
import com.interview.materials.feature.test.inditex.application.command.UploadAssetCommand;
import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidBase64EncodedAssetException;
import com.interview.materials.feature.test.inditex.application.validation.error.UnsupportedAssetContentTypeException;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.model.AssetId;
import com.interview.materials.feature.test.inditex.domain.port.in.usecase.UploadAssetUseCase;
import com.interview.materials.feature.test.inditex.infraestructure.mapper.AssetMapper;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadRequest;
import com.interview.materials.feature.test.inditex.infraestructure.web.dto.AssetFileUploadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadAssetServiceAdapterTest {

    @Mock
    private UploadAssetUseCase uploadAssetUseCase;

    @Mock
    private AssetValidator assetValidator;

    @Mock
    private AssetMapper assetMapper;

    @InjectMocks
    private UploadAssetServiceAdapter uploadAssetServiceImpl;

    @Test
    void shouldUploadAssetSuccessfully() {
        AssetFileUploadRequest request = new AssetFileUploadRequest("file.jpg", "image/jpeg", Base64.getEncoder().encodeToString("data".getBytes()));
        UploadAssetCommand command = UploadAssetCommand.builder()
                .filename(request.filename())
                .contentType(request.contentType())
                .encodedFile(request.encodedFile())
                .size(4L)
                .build();

        Asset domainAsset = Asset.builder()
                .id(AssetId.of(UUID.randomUUID()))
                .filename(command.filename())
                .contentType(command.contentType())
                .url("https://assets.cdn.fake/" + command.filename())
                .size(command.size())
                .uploadDate(LocalDateTime.now())
                .build();

        when(assetValidator.validateEncodedFile(anyString())).thenReturn(Mono.empty());
        when(assetValidator.validateContentType(anyString())).thenReturn(Mono.empty());
        when(uploadAssetUseCase.upload(any(Asset.class))).thenReturn(Mono.just(domainAsset));


        AssetFileUploadResponse expectedResponse = new AssetFileUploadResponse(domainAsset.getId().getValue().toString());

        when(assetMapper.toCommand(request)).thenReturn(command);
        when(assetMapper.toDomain(command, "https://assets.cdn.fake/file.jpg")).thenReturn(domainAsset);
        when(assetMapper.toResponse(domainAsset)).thenReturn(expectedResponse);

        Mono<AssetFileUploadResponse> result = uploadAssetServiceImpl.handle(request);

        StepVerifier.create(result)
                .expectNextMatches(resp -> resp.id().equals(expectedResponse.id()))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenBase64IsInvalid() {
        AssetFileUploadRequest request = new AssetFileUploadRequest("file.jpg", "not_base64!!", "image/png");

        when(assetValidator.validateEncodedFile("not_base64!!"))
                .thenReturn(Mono.error(new InvalidBase64EncodedAssetException("The encoded file is not valid base64.")));
        when(assetValidator.validateContentType(anyString()))
                .thenReturn(Mono.empty());

        Mono<AssetFileUploadResponse> result = uploadAssetServiceImpl.handle(request);

        StepVerifier.create(result)
                .expectError(InvalidBase64EncodedAssetException.class)
                .verify();
    }

    @Test
    void shouldFailWhenContentTypeIsInvalid() {
        AssetFileUploadRequest request = new AssetFileUploadRequest("file.jpg", "VGhpcyBpcyBhIGZha2UgZW5jb2RlZCBmaWxlIGJvZHk=", "other/png");

        when(assetValidator.validateEncodedFile(anyString())).thenReturn(Mono.empty());
        when(assetValidator.validateContentType("other/png"))
                .thenReturn(Mono.error(new UnsupportedAssetContentTypeException("Unsupported content type: other")));

        Mono<AssetFileUploadResponse> result = uploadAssetServiceImpl.handle(request);

        StepVerifier.create(result)
                .expectError(UnsupportedAssetContentTypeException.class)
                .verify();
    }

}

