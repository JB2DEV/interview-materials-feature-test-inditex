package com.interview.materials.feature.test.inditex.shared.context;

import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import static com.interview.materials.feature.test.inditex.shared.web.TraceWebFilter.TRACE_ID_KEY;

public class TraceIdHolder {

    public static final String DEFAULT_TRACE = "N/A";

    public static Mono<String> getTraceId() {
        return Mono.deferContextual(context -> Mono.just(getTraceId(context)));
    }

    public static String getTraceId(ContextView context) {
        return context.getOrDefault(TRACE_ID_KEY, DEFAULT_TRACE);
    }
}