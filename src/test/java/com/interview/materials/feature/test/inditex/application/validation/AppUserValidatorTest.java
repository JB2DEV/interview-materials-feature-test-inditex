package com.interview.materials.feature.test.inditex.application.validation;

import com.interview.materials.feature.test.inditex.application.validation.error.MissingCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AppUserValidatorTest {

    @InjectMocks
    private AppUserValidator appUserValidator;

    @Test
    void validate_validCredentials_succeeds() {
        String username = "testUser";
        String password = "securePassword123";
        
        Mono<Void> result = appUserValidator.validate(username, password);
        
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void validate_emptyUsername_throwsException() {
        String username = "";
        String password = "nonEmptyPassword";

        Mono<Void> result = appUserValidator.validate(username, password);
        
        StepVerifier.create(result)
                .verifyErrorSatisfies(error -> {
                    assert error instanceof MissingCredentialsException;
                    assert error.getMessage().equals("Username and password must not be empty.");
                });
    }

    @Test
    void validate_blankUsername_throwsException() {
        String username = "   ";
        String password = "nonEmptyPassword";

        Mono<Void> result = appUserValidator.validate(username, password);

        StepVerifier.create(result)
                .verifyErrorSatisfies(error -> {
                    assert error instanceof MissingCredentialsException;
                    assert error.getMessage().equals("Username and password must not be empty.");
                });
    }

    @Test
    void validate_emptyPassword_throwsException() {
        String username = "validUser";
        String password = "";
        
        Mono<Void> result = appUserValidator.validate(username, password);

        StepVerifier.create(result)
                .verifyErrorSatisfies(error -> {
                    assert error instanceof MissingCredentialsException;
                    assert error.getMessage().equals("Username and password must not be empty.");
                });
    }

    @Test
    void validate_blankPassword_throwsException() {
        String username = "validUser";
        String password = "   ";
        
        Mono<Void> result = appUserValidator.validate(username, password);
        
        StepVerifier.create(result)
                .verifyErrorSatisfies(error -> {
                    assert error instanceof MissingCredentialsException;
                    assert error.getMessage().equals("Username and password must not be empty.");
                });
    }

    @Test
    void validate_bothEmpty_throwsException() {
        String username = "";
        String password = "";
        
        Mono<Void> result = appUserValidator.validate(username, password);

        StepVerifier.create(result)
                .verifyErrorSatisfies(error -> {
                    assert error instanceof MissingCredentialsException;
                    assert error.getMessage().equals("Username and password must not be empty.");
                });
    }

    @Test
    void validate_bothBlank_throwsException() {
        String username = "   ";
        String password = "   ";

        Mono<Void> result = appUserValidator.validate(username, password);

        StepVerifier.create(result)
                .verifyErrorSatisfies(error -> {
                    assert error instanceof MissingCredentialsException;
                    assert error.getMessage().equals("Username and password must not be empty.");
                });
    }
}