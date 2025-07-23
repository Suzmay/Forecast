package com.jnu.weather.service;

import com.jnu.weather.domain.WeatherResponse;

public interface WeatherService {
    WeatherResponse accessThreeWithRedis(String city, String type);

    WeatherResponse accessThreeWithSnow(String city, String type);

    // 删除cacheWeatherData方法，改为按需加载策略
    // 只有用户查询时才从API拉取数据，避免浪费API配额
}
