package com.gdg.linking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LinkingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkingApplication.class, args);
	}

}
