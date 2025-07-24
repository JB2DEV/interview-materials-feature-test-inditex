package com.interview.materials.feature.test.inditex.infrastructure.adapter.out.security;

import com.interview.materials.feature.test.inditex.domain.port.out.security.JwtPort;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProviderAdapter implements JwtPort {

    private final JwtTokenProvider delegate;

    @Override
    public Mono<String> generateToken(String username, List<String> roles) {
        return Mono.fromCallable(() -> delegate.generateToken(username, roles));
    }

    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> delegate.validateToken(token));
    }

    @Override
    public Mono<Claims> getClaims(String token) {
        return Mono.fromCallable(() -> delegate.getClaims(token));
    }
}