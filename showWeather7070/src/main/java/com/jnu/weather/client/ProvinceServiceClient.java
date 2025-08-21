package com.jnu.weather.client;

import com.jnu.weather.po.Province;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "GETPROVINCELIST8090", fallback = com.jnu.weather.client.fallback.ProvinceServiceFallback.class)
public interface ProvinceServiceClient {
    
    @GetMapping("/api/province/FINDALL")
    List<Province> findAllProvinces();
    
    @GetMapping("/api/province/health")
    String health();
}
