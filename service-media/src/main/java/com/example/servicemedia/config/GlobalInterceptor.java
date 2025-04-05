package com.example.servicemedia.config;

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

        log.info("Media service interceptor intervene this request: {}, method: {}", request.getRequestURL(), request.getMethod());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}
