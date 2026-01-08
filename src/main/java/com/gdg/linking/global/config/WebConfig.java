package com.gdg.linking.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 형식상 패턴 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // 3. 필요한 메서드만 허용
                .allowCredentials(true) // 4. 쿠키/인증 헤더 허용 시 필수
                .maxAge(3600); // 5. Preflight 요청 캐싱 시간 설정 (성능 향상)
    }
}
