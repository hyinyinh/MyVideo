package com.hy.tiktok.config;

import com.hy.tiktok.intercepter.PassportInterceptor;
import com.hy.tiktok.intercepter.RefreshTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/18 19:33
 */

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor(){
        return new PassportInterceptor();
    }

    @Bean
    public RefreshTokenInterceptor refreshTokenInterceptor(){
        return new RefreshTokenInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor())
                .addPathPatterns("/passport/getSMSCode")
                .order(1);

        registry.addInterceptor(refreshTokenInterceptor())
                .addPathPatterns("/**")
                .order(0);
    }
}
