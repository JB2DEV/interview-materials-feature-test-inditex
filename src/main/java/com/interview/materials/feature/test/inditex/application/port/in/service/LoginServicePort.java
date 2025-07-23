package com.interview.materials.feature.test.inditex.application.port.in.service;

import com.interview.materials.feature.test.inditex.application.command.LoginCommand;
import com.interview.materials.feature.test.inditex.application.command.LoginResponse;
import reactor.core.publisher.Mono;

public interface LoginServicePort {
    Mono<LoginResponse> login(LoginCommand command);
}
