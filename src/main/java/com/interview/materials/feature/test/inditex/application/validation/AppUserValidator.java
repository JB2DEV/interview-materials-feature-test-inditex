package com.interview.materials.feature.test.inditex.application.validation;

import com.interview.materials.feature.test.inditex.application.validation.error.MissingCredentialsException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AppUserValidator {

    public Mono<Void> validate(String username, String password) {
        return Mono.fromRunnable(() -> {
            if (username.isBlank() || password.isBlank()) {
                throw new MissingCredentialsException("Username and password must not be empty.");
            }
        });
    }
}