package com.jnu.weather.service;

import com.jnu.weather.domain.WeatherResponse;

public interface WeatherService {
    WeatherResponse accessThreeWithRedis(String city, String type);

    WeatherResponse accessThreeWithSnow(String city, String type);

    // 缓存天气数据
    void cacheWeatherData(String city);
}
