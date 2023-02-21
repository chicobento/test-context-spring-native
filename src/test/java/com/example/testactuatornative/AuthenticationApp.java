package com.example.testactuatornative;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableAutoConfiguration
@Slf4j
public class AuthenticationApp {

	@Bean
	TestController controller() {
		return new TestController();
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApp.class, args);
	}

}
