package com.interview.materials.feature.test.inditex.domain.port.out.security;


import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

import java.util.List;

public interface JwtPort {
    Mono<String> generateToken(String username, List<String> roles);
    Mono<Boolean> validateToken(String token);
    Mono<Claims> getClaims(String token);
}