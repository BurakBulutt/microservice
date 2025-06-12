package com.example.servicemedia.config.feign;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeignConfigConstants {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_X_B3_TRACE_ID = "X-B3-TraceId";
    public static final String HEADER_X_B3_SPAN_ID = "X-B3-SpanId";
    public static final String HEADER_X_B3_SAMPLED = "X-B3-Sampled";
}
