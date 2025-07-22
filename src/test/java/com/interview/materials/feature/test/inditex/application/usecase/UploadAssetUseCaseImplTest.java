package com.interview.materials.feature.test.inditex.application.usecase;

import com.interview.materials.feature.test.inditex.domain.model.Asset;
import com.interview.materials.feature.test.inditex.domain.model.AssetId;
import com.interview.materials.feature.test.inditex.domain.repository.AssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadAssetUseCaseImplTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private UploadAssetUseCaseImpl uploadAssetUseCaseImpl;

    @Test
    void upload_shouldReturnSavedAsset_whenRepositorySucceeds() {
        Asset inputAsset = createTestAsset(null);
        Asset savedAsset = createTestAsset(UUID.randomUUID());

        when(assetRepository.save(any(Asset.class)))
                .thenReturn(Mono.just(savedAsset));

        StepVerifier.create(uploadAssetUseCaseImpl.upload(inputAsset))
                .expectNextMatches(asset ->
                        asset.getId() != null &&
                                asset.getFilename().equals("test-file.png") &&
                                asset.getContentType().equals("image/png"))
                .verifyComplete();
    }

    @Test
    void upload_shouldCallRepositoryWithCorrectAsset() {
        Asset inputAsset = createTestAsset(null);
        Asset savedAsset = createTestAsset(UUID.randomUUID());

        when(assetRepository.save(any(Asset.class)))
                .thenReturn(Mono.just(savedAsset));

        uploadAssetUseCaseImpl.upload(inputAsset).block();

        verify(assetRepository, times(1)).save(inputAsset);
    }

    private Asset createTestAsset(UUID id) {
        return Asset.builder()
                .id(id != null ? AssetId.of(id) : null)
                .filename("test-file.png")
                .contentType("image/png")
                .url("http://example.com/file.png")
                .size(1024L)
                .uploadDate(LocalDateTime.now())
                .build();
    }
}