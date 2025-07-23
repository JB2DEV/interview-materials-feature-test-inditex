package com.interview.materials.feature.test.inditex.infrastructure.config;

import com.interview.materials.feature.test.inditex.infrastructure.adapter.in.security.CustomAccessDeniedHandler;
import com.interview.materials.feature.test.inditex.infrastructure.adapter.in.security.CustomAuthenticationEntryPoint;
import com.interview.materials.feature.test.inditex.infrastructure.adapter.in.security.JwtSecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    private final JwtSecurityContextRepository contextRepository;
    private final CustomAuthenticationEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(contextRepository)
                .exceptionHandling(spec -> spec
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/mgmt/1/assets/actions/upload/**").hasRole("ADMIN")
                        .anyExchange().permitAll()
                )
                .build();
    }



}