package com.example.servicemedia.config.feign;

import feign.RequestInterceptor;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final Tracer tracer;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();

            if (requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

                final String token = request.getHeader(FeignConfigConstants.HEADER_AUTHORIZATION);

                if (StringUtils.hasLength(token)) {
                    requestTemplate.header(FeignConfigConstants.HEADER_AUTHORIZATION,token);
                }
            }

            if (tracer != null && tracer.currentSpan() != null) {
                TraceContext context = Objects.requireNonNull(tracer.currentSpan()).context();
                final String traceId = context.traceId();
                final String spanId = context.spanId();

                requestTemplate.header(FeignConfigConstants.HEADER_X_B3_TRACE_ID, traceId);
                requestTemplate.header(FeignConfigConstants.HEADER_X_B3_SPAN_ID, spanId);
                requestTemplate.header(FeignConfigConstants.HEADER_X_B3_SAMPLED, "1");
            }
        };
    }
}
