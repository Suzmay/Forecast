package com.jnu.weather.job;

import com.jnu.weather.po.City;
import com.jnu.weather.service.CityService;
import com.jnu.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public class ScheduledTaskJob {
    @Autowired
    WeatherService weatherService;
    @Autowired
    CityService cityService;

    /**
     * 每隔15分钟拉取天气数据缓存到Redis
     */
    @Scheduled(cron="0/15 * * * * ?")
    public void cacheWeatherData(){
        //拉取城市的天气数据
        List<City> cityList = cityService.findAllCity();
        for(City city : cityList){
            //需要从第三方接口获取最新鲜的数据
//            WeatherResponse weatherResponse=weatherService.queryWeatherByCityId(cityArr[i]);
//            System.out.println("query result:"+weatherResponse.getResult().getArea());
            weatherService.cacheWeatherData(city.getCityId());
        }
    }
}
