package com.gdg.linking.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:[*]",           // 로컬의 모든 포트 허용
                        "https://*.thelinking.store"      // api., www. 등 모든 서브도메인 허용
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // 3. 필요한 메서드만 허용
                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With")
                .allowCredentials(true) // 4. 쿠키/인증 헤더 허용 시 필수
                .maxAge(3600); // 5. Preflight 요청 캐싱 시간 설정 (성능 향상)
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("https://thelinking.store").description("Production Server"), // 배포 환경
                        new Server().url("http://localhost:8080").description("Local Server") // 로컬 환경
                ));
    }
}
