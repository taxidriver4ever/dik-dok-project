package org.example.tiktokproject.config;

import jakarta.annotation.Resource;
import org.example.tiktokproject.interceptor.LoggedInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private LoggedInterceptor loggedInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggedInterceptor)
                .excludePathPatterns("/user/**")
                .excludePathPatterns("/video/random")
                .excludePathPatterns("/video/title")
                .excludePathPatterns("/video/data")
                .excludePathPatterns("/live/living-all-data")
                .excludePathPatterns("/video/status");
    }
}

