package com.interview.materials.feature.test.inditex.infrastructure.mapper;

import com.interview.materials.feature.test.inditex.application.command.LoginCommand;
import com.interview.materials.feature.test.inditex.domain.model.AppUser;
import com.interview.materials.feature.test.inditex.infrastructure.web.dto.LoginRequest;
import com.interview.materials.feature.test.inditex.application.command.LoginResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class AppUserMapper {

    public LoginCommand toCommand(LoginRequest credentials) {
        return new LoginCommand(credentials.username(), credentials.password());
    }

    public AppUser toDomain(LoginCommand command) {
        return AppUser.builder()
                .username(command.username())
                .password(command.password())
                .roles(Collections.emptyList())
                .build();
    }

    public LoginResponse toResponse(String token) {
        return new LoginResponse(token);
    }
}