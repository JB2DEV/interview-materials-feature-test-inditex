package com.interview.materials.feature.test.inditex.infrastructure.adapter.out.repository;

import com.interview.materials.feature.test.inditex.domain.model.AppUser;
import com.interview.materials.feature.test.inditex.infrastructure.config.SecurityProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginRepositoryAdapterTest {

    @Mock
    private SecurityProperties securityProperties;

    @Mock
    private SecurityProperties.AdminUser adminUser;

    @InjectMocks
    private LoginRepositoryAdapter loginRepositoryAdapter;

    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "securePassword";
    private static final String INVALID_USERNAME = "wrongUser";
    private static final String INVALID_PASSWORD = "wrongPassword";

    @Test
    void findByCredentials_withValidCredentials_returnsAdminUser() {
        when(securityProperties.admin()).thenReturn(adminUser);
        when(adminUser.username()).thenReturn(VALID_USERNAME);
        when(adminUser.password()).thenReturn(VALID_PASSWORD);

        Mono<AppUser> result = loginRepositoryAdapter.findByCredentials(VALID_USERNAME, VALID_PASSWORD);

        StepVerifier.create(result)
                .expectNextMatches(user ->
                        user.getUsername().equals(VALID_USERNAME) &&
                                user.getPassword().equals(VALID_PASSWORD) &&
                                user.getRoles().equals(List.of("ROLE_ADMIN"))
                )
                .verifyComplete();
    }

    @Test
    void findByCredentials_withInvalidCredentials_returnsEmpty() {
        when(securityProperties.admin()).thenReturn(adminUser);
        when(adminUser.username()).thenReturn(VALID_USERNAME);
        when(adminUser.password()).thenReturn(VALID_PASSWORD);

        Mono<AppUser> result = loginRepositoryAdapter.findByCredentials(INVALID_USERNAME, INVALID_PASSWORD);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void findByCredentials_partialInvalidCredentials_returnsEmpty() {
        when(securityProperties.admin()).thenReturn(adminUser);
        when(adminUser.username()).thenReturn(VALID_USERNAME);
        when(adminUser.password()).thenReturn(VALID_PASSWORD);

        StepVerifier.create(loginRepositoryAdapter.findByCredentials(VALID_USERNAME, INVALID_PASSWORD))
                .verifyComplete();

        StepVerifier.create(loginRepositoryAdapter.findByCredentials(INVALID_USERNAME, VALID_PASSWORD))
                .verifyComplete();
    }
}