package com.gdg.linking;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing
public class LinkingApplication {

	@PostConstruct
	public void started() {
		// TimeZone 클래스의 정적 메서드 setDefault를 호출
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
	public static void main(String[] args) {
		SpringApplication.run(LinkingApplication.class, args);
	}

}
