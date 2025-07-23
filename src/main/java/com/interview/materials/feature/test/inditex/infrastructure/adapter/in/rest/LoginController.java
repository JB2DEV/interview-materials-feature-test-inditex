package com.interview.materials.feature.test.inditex.infrastructure.adapter.in.rest;

import com.interview.materials.feature.test.inditex.application.command.LoginCommand;
import com.interview.materials.feature.test.inditex.application.port.in.service.LoginServicePort;
import com.interview.materials.feature.test.inditex.infrastructure.mapper.AppUserMapper;
import com.interview.materials.feature.test.inditex.infrastructure.web.dto.LoginRequest;
import com.interview.materials.feature.test.inditex.application.command.LoginResponse;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mgmt/1/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginServicePort loginServicePort;
    private final AppUserMapper appUserMapper;

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest requestDto) {
        LoginCommand command = appUserMapper.toCommand(requestDto);
        return TraceIdHolder.getTraceId()
                .flatMap(traceId -> {
                    log.info("[traceId={}] Login request received for user '{}'", traceId, command.username());
                    return loginServicePort.login(command)
                            .map(ResponseEntity::ok)
                            .switchIfEmpty(Mono.defer(() -> {
                                log.warn("[traceId={}] Login failed for user '{}'", traceId, command.username());
                                return Mono.just(ResponseEntity.status(401).build());
                            }));
                });
    }
}