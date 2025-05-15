package com.example.servicemedia.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
@Component
public class CategoryInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!request.getMethod().equals(HttpMethod.GET.name())) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        log.info("Category interceptor intervene this request: {}", request.getRequestURL());

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
