package com.jnu.weather.eureka9999;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class Eureka9999Application {

    public static void main(String[] args) {
        SpringApplication.run(Eureka9999Application.class, args);
    }

}
