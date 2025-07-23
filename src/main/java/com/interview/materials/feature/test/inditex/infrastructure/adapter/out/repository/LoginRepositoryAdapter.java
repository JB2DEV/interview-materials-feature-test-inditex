package com.interview.materials.feature.test.inditex.infrastructure.adapter.out.repository;

import com.interview.materials.feature.test.inditex.infrastructure.config.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.List;
import com.interview.materials.feature.test.inditex.domain.model.AppUser;
import com.interview.materials.feature.test.inditex.domain.port.out.repository.LoginRepositoryPort;

@Component
@RequiredArgsConstructor
public class LoginRepositoryAdapter implements LoginRepositoryPort {

    private final SecurityProperties securityProperties;

    @Override
    public Mono<AppUser> findByCredentials(String username, String password) {
        if (securityProperties.admin().username().equals(username)
                && securityProperties.admin().password().equals(password)) {
            return Mono.just(AppUser.builder()
                    .username(username)
                    .password(password)
                    .roles(List.of("ROLE_ADMIN"))
                    .build());
        }
        return Mono.empty();
    }
}