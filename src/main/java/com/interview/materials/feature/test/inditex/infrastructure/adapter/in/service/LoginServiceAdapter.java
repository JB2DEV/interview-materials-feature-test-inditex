package com.interview.materials.feature.test.inditex.infrastructure.adapter.in.service;

import com.interview.materials.feature.test.inditex.application.command.LoginCommand;
import com.interview.materials.feature.test.inditex.application.port.in.service.LoginServicePort;
import com.interview.materials.feature.test.inditex.application.validation.AppUserValidator;
import com.interview.materials.feature.test.inditex.domain.exception.UnauthorizedUserException;
import com.interview.materials.feature.test.inditex.domain.model.AppUser;
import com.interview.materials.feature.test.inditex.domain.port.in.usecase.LoginUseCasePort;
import com.interview.materials.feature.test.inditex.domain.port.out.security.JwtPort;
import com.interview.materials.feature.test.inditex.infrastructure.mapper.AppUserMapper;
import com.interview.materials.feature.test.inditex.application.command.LoginResponse;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceAdapter implements LoginServicePort {

    private final LoginUseCasePort loginUseCasePort;
    private final AppUserValidator appUserValidator;
    private final AppUserMapper appUserMapper;
    private final JwtPort jwtPort;

    @Override
    public Mono<LoginResponse> login(LoginCommand command) {
        return appUserValidator.validate(command.username(), command.password())
                .then(Mono.defer(() -> {
                    AppUser domainUser = appUserMapper.toDomain(command);

                    return TraceIdHolder.getTraceId()
                            .flatMap(traceId -> {
                                log.info("[traceId={}] Executing login use case", traceId);
                                return loginUseCasePort.login(domainUser)
                                        .switchIfEmpty(Mono.defer(() -> {
                                            log.warn("[traceId={}] Authentication failed for user '{}'",
                                                    traceId, command.username());
                                            return Mono.error(new UnauthorizedUserException("Invalid credentials"));
                                        }))
                                        .doOnSuccess(validUser -> log.info(
                                                "[traceId={}] User '{}' authenticated successfully",
                                                traceId,
                                                validUser.getUsername()
                                        ))
                                        .flatMap(validUser -> generateToken(validUser, traceId))
                                        .map(appUserMapper::toResponse);
                            });
                }));
    }

    private Mono<String> generateToken(AppUser validUser, String traceId) {
        return jwtPort.generateToken(validUser.getUsername(), validUser.getRoles())
                .doOnNext(token -> log.debug(
                        "[traceId={}] Token generated for user '{}'",
                        traceId,
                        validUser.getUsername()
                ));
    }
}