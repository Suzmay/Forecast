package com.jnu.weather.eureka8888;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class Eureka8888Application {

	public static void main(String[] args) {
		SpringApplication.run(Eureka8888Application.class, args);
	}

}
