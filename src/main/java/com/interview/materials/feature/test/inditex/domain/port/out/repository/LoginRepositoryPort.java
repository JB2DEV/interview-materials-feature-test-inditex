package com.interview.materials.feature.test.inditex.domain.port.out.repository;

import reactor.core.publisher.Mono;
import com.interview.materials.feature.test.inditex.domain.model.AppUser;

public interface LoginRepositoryPort {
    Mono<AppUser> findByCredentials(String username, String password);
}