package com.interview.materials.feature.test.inditex.domain.port.in.usecase;

import reactor.core.publisher.Mono;
import com.interview.materials.feature.test.inditex.domain.model.AppUser;

public interface LoginUseCasePort {
    Mono<AppUser> login(AppUser user);
}