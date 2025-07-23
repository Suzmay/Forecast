package com.jnu.weather.job;

import com.jnu.weather.po.City;
import com.jnu.weather.service.WeatherService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ScheduledTaskJob {
    @Autowired
    WeatherService weatherService;
    @Autowired
    RestTemplate loadBalancedRestTemplate;

    /**
     * 注释掉定时任务，改为按需加载策略
     * 只有用户查询时才从API拉取数据，避免浪费API配额
     */
    /*
    @Scheduled(cron="0/15 * * * * ?")
    public void cacheWeatherData(){
        try {
            // 通过负载均衡调用城市服务获取城市列表
            ObjectMapper objectMapper = new ObjectMapper();
            Object cityListObj = loadBalancedRestTemplate.getForObject("http://GETCITYLIST8080/api/city/FINDALL", Object.class);
            List<City> cityList = objectMapper.convertValue(cityListObj, new TypeReference<List<City>>() {});
            
            System.out.println("[ScheduledTask] 开始缓存天气数据，城市数量: " + cityList.size());
            
            for(City city : cityList){
                try {
                    // 缓存天气数据到Redis
                    weatherService.cacheWeatherData(city.getCityId());
                    System.out.println("[ScheduledTask] 成功缓存城市: " + city.getCity() + " (ID: " + city.getCityId() + ")");
                } catch (Exception e) {
                    System.out.println("[ScheduledTask] 缓存城市 " + city.getCity() + " 失败: " + e.getMessage());
                }
            }
            System.out.println("[ScheduledTask] 天气数据缓存任务完成");
        } catch (Exception e) {
            System.out.println("[ScheduledTask] 定时任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    */
}
