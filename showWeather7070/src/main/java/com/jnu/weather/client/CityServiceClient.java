package com.jnu.weather.client;

import com.jnu.weather.po.City;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "GETCITYLIST8080", fallback = com.jnu.weather.client.fallback.CityServiceFallback.class)
public interface CityServiceClient {
    
    @GetMapping("/api/city/FINDALL")
    List<City> findAllCities();
    
    @GetMapping("/api/city/FINDALLBYFATHER")
    List<City> findCitiesByProvince(@RequestParam("provinceId") String provinceId);
    
    @GetMapping("/api/city/health")
    String health();
}
