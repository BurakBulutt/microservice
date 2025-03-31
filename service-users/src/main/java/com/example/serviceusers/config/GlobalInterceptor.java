package com.example.serviceusers.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
@Component
public class GlobalInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!request.getMethod().equals(HttpMethod.GET.name())) {
            return true;
        }

        String correlationId = request.getHeader("X-Correlation-Id");
        String userId = request.getHeader("X-User-Id");

        if (correlationId != null) {
            MDC.put("correlationId", correlationId);
        }
        if (userId != null) {
            MDC.put("userId", userId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}
