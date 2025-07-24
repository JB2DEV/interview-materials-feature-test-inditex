package com.interview.materials.feature.test.inditex.application.usecase;

import com.interview.materials.feature.test.inditex.domain.port.in.usecase.LoginUseCasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.interview.materials.feature.test.inditex.domain.model.AppUser;
import com.interview.materials.feature.test.inditex.domain.port.out.repository.LoginRepositoryPort;

@Component
@RequiredArgsConstructor
public class LoginUseCase implements LoginUseCasePort {

    private final LoginRepositoryPort loginRepository;

    @Override
    public Mono<AppUser> login(AppUser user) {
        return loginRepository.findByCredentials(user.getUsername(), user.getPassword());
    }
}