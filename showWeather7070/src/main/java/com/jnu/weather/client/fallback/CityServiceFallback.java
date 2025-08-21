package com.jnu.weather.client.fallback;

import com.jnu.weather.client.CityServiceClient;
import com.jnu.weather.po.City;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CityServiceFallback implements CityServiceClient {
    
    @Override
    public List<City> findAllCities() {
        System.out.println("[Feign] 城市服务不可用，返回空列表");
        return Collections.emptyList();
    }
    
    @Override
    public List<City> findCitiesByProvince(String provinceId) {
        System.out.println("[Feign] 城市服务不可用，返回空列表，省份ID: " + provinceId);
        return Collections.emptyList();
    }
    
    @Override
    public String health() {
        return "City Service is unavailable (fallback)";
    }
}
