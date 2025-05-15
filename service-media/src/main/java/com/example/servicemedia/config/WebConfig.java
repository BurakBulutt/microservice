package com.example.servicemedia.config;

import com.example.servicemedia.interceptor.CategoryInterceptor;
import com.example.servicemedia.interceptor.ContentInterceptor;
import com.example.servicemedia.interceptor.GlobalInterceptor;
import com.example.servicemedia.interceptor.MediaInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final GlobalInterceptor globalInterceptor;
    private final ContentInterceptor contentInterceptor;
    private final MediaInterceptor mediaInterceptor;
    private final CategoryInterceptor categoryInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(contentInterceptor)
                .addPathPatterns("/contents/**");
        registry.addInterceptor(mediaInterceptor)
                .addPathPatterns("/medias/**");
        registry.addInterceptor(categoryInterceptor)
                .addPathPatterns("/categories/**");
    }
}
