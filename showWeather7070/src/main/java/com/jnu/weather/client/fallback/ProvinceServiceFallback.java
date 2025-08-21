package com.jnu.weather.client.fallback;

import com.jnu.weather.client.ProvinceServiceClient;
import com.jnu.weather.po.Province;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProvinceServiceFallback implements ProvinceServiceClient {
    
    @Override
    public List<Province> findAllProvinces() {
        System.out.println("[Feign] 省份服务不可用，返回空列表");
        return Collections.emptyList();
    }
    
    @Override
    public String health() {
        return "Province Service is unavailable (fallback)";
    }
}
