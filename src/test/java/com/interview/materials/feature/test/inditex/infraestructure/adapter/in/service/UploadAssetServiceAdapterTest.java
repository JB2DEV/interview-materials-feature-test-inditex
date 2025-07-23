package com.interview.materials.feature.test.inditex.infraestructure.adapter.in.service;

import com.interview.materials.feature.test.inditex.application.command.UploadAssetCommand;
import com.interview.materials.feature.test.inditex.application.validation.AssetValidator;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidBase64EncodedAssetException;
import com.interview.materials.feature.test.inditex.application.validation.error.UnsupportedAssetContentTypeException;
import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.model.AssetId;
import com.interview.materials.feature.test.inditex.domain.port.in.usecase.UploadAssetUseCasePort;
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
    private UploadAssetUseCasePort uploadAssetUseCasePort;

    @Mock
    private AssetValidator assetValidator;

    @Mock
    private AssetMapper assetMapper;

    @InjectMocks
    private UploadAssetServiceAdapter uploadAssetServiceImpl;

    @Test
    void shouldUploadAssetSuccessfully() {
        UploadAssetCommand command = UploadAssetCommand.builder()
                .filename("file.jpg")
                .contentType("image/jpeg")
                .encodedFile(Base64.getEncoder().encodeToString("data".getBytes()))
                .size(4L)
                .build();

        String expectedUrl = "https://assets.cdn.fake/" + command.filename();

        Asset domainAsset = Asset.builder()
                .id(AssetId.of(UUID.randomUUID()))
                .filename(command.filename())
                .contentType(command.contentType())
                .url(expectedUrl)
                .size(command.size())
                .uploadDate(LocalDateTime.now())
                .build();

        when(assetValidator.validateEncodedFile(anyString())).thenReturn(Mono.empty());
        when(assetValidator.validateContentType(anyString())).thenReturn(Mono.empty());
        when(uploadAssetUseCasePort.upload(any(Asset.class))).thenReturn(Mono.just(domainAsset));
        when(assetMapper.toDomain(command, expectedUrl)).thenReturn(domainAsset);

        Mono<Asset> result = uploadAssetServiceImpl.handle(command);

        StepVerifier.create(result)
                .expectNextMatches(asset ->
                        asset.getId().equals(domainAsset.getId()) &&
                                asset.getUrl().equals(expectedUrl)
                )
                .verifyComplete();
    }

    @Test
    void shouldFailWhenBase64IsInvalid() {
        UploadAssetCommand command = UploadAssetCommand.builder()
                .filename("file.jpg")
                .contentType("image/png")
                .encodedFile("not_base64!!")
                .build();

        when(assetValidator.validateEncodedFile("not_base64!!"))
                .thenReturn(Mono.error(new InvalidBase64EncodedAssetException("Invalid base64")));

        Mono<Asset> result = uploadAssetServiceImpl.handle(command);

        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof InvalidBase64EncodedAssetException &&
                                ex.getMessage().contains("Invalid base64")
                )
                .verify();
    }

    @Test
    void shouldFailWhenContentTypeIsInvalid() {
        UploadAssetCommand command = UploadAssetCommand.builder()
                .filename("file.jpg")
                .contentType("other/png")
                .encodedFile("VGhpcyBpcyBhIGZha2UgZW5jb2RlZCBmaWxlIGJvZHk=")
                .build();

        when(assetValidator.validateEncodedFile(anyString())).thenReturn(Mono.empty());
        when(assetValidator.validateContentType("other/png"))
                .thenReturn(Mono.error(
                        new UnsupportedAssetContentTypeException("Unsupported type: other")
                ));

        Mono<Asset> result = uploadAssetServiceImpl.handle(command);

        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof UnsupportedAssetContentTypeException &&
                                ex.getMessage().contains("Unsupported type")
                )
                .verify();
    }

}

