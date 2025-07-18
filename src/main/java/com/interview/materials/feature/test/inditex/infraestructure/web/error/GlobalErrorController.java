package com.interview.materials.feature.test.inditex.infraestructure.web.error;

import com.interview.materials.feature.test.inditex.application.validation.error.InvalidBase64EncodedAssetException;
import com.interview.materials.feature.test.inditex.application.validation.error.UnsupportedAssetContentTypeException;
import com.interview.materials.feature.test.inditex.domain.exception.DomainEntityNotFoundException;
import com.interview.materials.feature.test.inditex.domain.exception.DomainValidationException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalErrorController {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred.");
    }

    @ExceptionHandler(DomainEntityNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(DomainEntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DomainValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDomainValidation(DomainValidationException ex) {
        log.warn("Domain validation failed: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler({
            InvalidBase64EncodedAssetException.class,
            UnsupportedAssetContentTypeException.class
    })
    public Mono<ResponseEntity<ErrorResponse>> handleApplicationValidation(RuntimeException ex) {
        log.warn("Application validation error: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violations: {}", ex.getMessage());

        List<ErrorResponse.FieldError> errors = ex.getConstraintViolations().stream()
                .map(v -> new ErrorResponse.FieldError(
                        ((PathImpl) v.getPropertyPath()).getLeafNode().getName(),
                        v.getMessage()))
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid input fields", errors);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebExchangeBind(WebExchangeBindException ex) {
        log.warn("Validation errors on request: {}", ex.getMessage());

        List<ErrorResponse.FieldError> errors = ex.getFieldErrors().stream()
                .map(err -> new ErrorResponse.FieldError(
                        err.getField(),
                        err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid value"))
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "One or more fields are invalid", errors);
    }

    private Mono<ResponseEntity<ErrorResponse>> buildResponse(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                nowFormatted(),
                null
        );
        return Mono.just(ResponseEntity.status(status).body(error));
    }

    private Mono<ResponseEntity<ErrorResponse>> buildResponse(HttpStatus status, String message, List<ErrorResponse.FieldError> errors) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                nowFormatted(),
                errors
        );
        return Mono.just(ResponseEntity.status(status).body(error));
    }

    private String nowFormatted() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
    }
}