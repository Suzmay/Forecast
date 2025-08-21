package com.jnu.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.event.EventListener;

@SpringBootApplication(scanBasePackages = "com.jnu.weather")
@EnableEurekaClient
public class GetCityList8080Application {

    public static void main(String[] args) {
        SpringApplication.run(GetCityList8080Application.class, args);
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("[INFO] City Service 启动完成，端口: 8080");
        System.out.println("[INFO] 健康检查地址: http://localhost:8080/api/city/health");
        System.out.println("[INFO] 城市查询地址: http://localhost:8080/api/city/FINDALL");
    }

}
