package com.jnu.weather.service;

import java.util.List;

import com.jnu.weather.po.City;

public interface CityService {
    // 查询城市列表数据
    List<City> findAllCity();
}
