package com.interview.materials.feature.test.inditex.infrastructure.adapter.in.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.security.core.context.SecurityContextImpl;

@Component
@RequiredArgsConstructor
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtAuthenticationManager authManager;

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String token = extractToken(exchange.getRequest());

        if (token != null) {
            Authentication auth = new UsernamePasswordAuthenticationToken(null, token);
            return authManager.authenticate(auth).map(SecurityContextImpl::new);
        }

        return Mono.empty();
    }

    private String extractToken(ServerHttpRequest request) {
        String bearer = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }
}