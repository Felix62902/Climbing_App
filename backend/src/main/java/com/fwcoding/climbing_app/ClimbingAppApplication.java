package com.fwcoding.climbing_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ClimbingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClimbingAppApplication.class, args);
	}

}
