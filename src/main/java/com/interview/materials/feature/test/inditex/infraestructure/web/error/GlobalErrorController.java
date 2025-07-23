package com.interview.materials.feature.test.inditex.infraestructure.web.error;

import com.interview.materials.feature.test.inditex.application.validation.error.InvalidDateRangeException;
import com.interview.materials.feature.test.inditex.application.validation.error.InvalidSortDirectionException;
import com.interview.materials.feature.test.inditex.application.validation.error.UnsupportedAssetContentTypeException;
import com.interview.materials.feature.test.inditex.shared.context.TraceIdHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalErrorController {

    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(NoResourceFoundException ex) {
        return TraceIdHolder.getTraceId().flatMap(traceId -> {
            log.warn("[traceId={}] Resource not found: {}", traceId, ex.getMessage());
            return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        });
    }

    @ExceptionHandler({
            UnsupportedAssetContentTypeException.class,
            InvalidDateRangeException.class,
            InvalidSortDirectionException.class
    })
    public Mono<ResponseEntity<ErrorResponse>> handleApplicationValidation(RuntimeException ex) {
        return TraceIdHolder.getTraceId().flatMap(traceId -> {
            log.warn("[traceId={}] Application validation error: {}", traceId, ex.getMessage());
            return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        });
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebExchangeBind(WebExchangeBindException ex) {
        return TraceIdHolder.getTraceId().flatMap(traceId -> {
            log.warn("[traceId={}] Validation errors on request: {}", traceId, ex.getMessage());

            List<ErrorResponse.FieldError> errors = ex.getFieldErrors().stream()
                    .map(err -> new ErrorResponse.FieldError(
                            err.getField(),
                            err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid value"))
                    .toList();

            return buildResponse(HttpStatus.BAD_REQUEST, "One or more fields are invalid", errors);
        });
    }

    private Mono<ResponseEntity<ErrorResponse>> buildResponse(HttpStatus status, String message) {
        return Mono.just(ResponseEntity.status(status).body(
                new ErrorResponse(
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        nowFormatted(),
                        null
                )
        ));
    }

    private Mono<ResponseEntity<ErrorResponse>> buildResponse(HttpStatus status, String message, List<ErrorResponse.FieldError> errors) {
        return Mono.just(ResponseEntity.status(status).body(
                new ErrorResponse(
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        nowFormatted(),
                        errors
                )
        ));
    }

    private String nowFormatted() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());
    }
}