package com.interview.materials.feature.test.inditex.application.usecase;

import com.interview.materials.feature.test.inditex.domain.model.AppUser;
import com.interview.materials.feature.test.inditex.domain.port.out.repository.LoginRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private LoginRepositoryPort loginRepository;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    void login_ShouldReturnUser_WhenCredentialsMatch() {
        AppUser expectedUser = AppUser.builder().username("testUser").password("validPassword").roles(List.of("ROLE_ADMIN")).build();
        when(loginRepository.findByCredentials(anyString(), anyString()))
                .thenReturn(Mono.just(expectedUser));

        Mono<AppUser> result = loginUseCase.login(expectedUser);

        StepVerifier.create(result)
                .expectNext(expectedUser)
                .verifyComplete();

        verify(loginRepository).findByCredentials("testUser", "validPassword");
    }

    @Test
    void login_ShouldCallRepositoryWithCorrectParameters() {
        AppUser inputUser = AppUser.builder().username("testUser").password("validPassword").build();
        when(loginRepository.findByCredentials("testUser", "validPassword"))
                .thenReturn(Mono.empty());

        loginUseCase.login(inputUser);

        verify(loginRepository).findByCredentials("testUser", "validPassword");
    }

    @Test
    void login_ShouldPropagateError_WhenRepositoryFails() {
        RuntimeException error = new RuntimeException("Database error");
        when(loginRepository.findByCredentials(anyString(), anyString()))
                .thenReturn(Mono.error(error));
        AppUser inputUser = AppUser.builder().username("anyUser").password("anyPass").build();

        Mono<AppUser> result = loginUseCase.login(inputUser);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e.equals(error))
                .verify();

        verify(loginRepository).findByCredentials("anyUser", "anyPass");
    }
}