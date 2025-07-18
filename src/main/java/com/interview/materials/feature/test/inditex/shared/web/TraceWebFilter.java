package com.interview.materials.feature.test.inditex.shared.web;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceWebFilter implements WebFilter {

    public static final String TRACE_ID_KEY = "traceId";

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String traceId = UUID.randomUUID().toString();
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(TRACE_ID_KEY, traceId));
    }
}