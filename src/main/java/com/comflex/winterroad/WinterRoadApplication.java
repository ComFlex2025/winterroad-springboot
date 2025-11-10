package com.comflex.winterroad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WinterRoadApplication {

	public static void main(String[] args) {
		SpringApplication.run(WinterRoadApplication.class, args);
	}

}
