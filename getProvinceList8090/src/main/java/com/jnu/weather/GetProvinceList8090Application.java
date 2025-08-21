package com.jnu.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication(scanBasePackages = "com.jnu.weather")
@EnableEurekaClient
public class GetProvinceList8090Application {

    public static void main(String[] args) {
        SpringApplication.run(GetProvinceList8090Application.class, args);
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("[INFO] Province Service 启动完成，端口: 8090");
        System.out.println("[INFO] 健康检查地址: http://localhost:8090/api/province/health");
        System.out.println("[INFO] 省份查询地址: http://localhost:8090/api/province/FINDALL");
    }

}
