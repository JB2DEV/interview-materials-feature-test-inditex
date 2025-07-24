package com.interview.materials.feature.test.inditex.infrastructure.adapter.in.security;

import com.interview.materials.feature.test.inditex.domain.port.out.security.JwtPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtPort jwtPort;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return jwtPort.validateToken(token)
                .flatMap(valid -> {
                    if (Boolean.FALSE.equals(valid)) {
                        return Mono.empty();
                    }
                    return jwtPort.getClaims(token)
                            .map(claims -> {
                                String username = claims.getSubject();
                                List<String> roles = claims.get("roles", List.class);
                                List<SimpleGrantedAuthority> authorities = roles.stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .toList();
                                return new UsernamePasswordAuthenticationToken(username, token, authorities);
                            });
                });
    }
}