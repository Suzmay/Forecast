package com.jnu.weather.service;

import com.jnu.weather.po.City;
import java.util.List;

public interface CityService {
    List<City> FINDALLCITY();
    List<City> FINDALLBYCITYLIKE(String city);
    List<City> FINDALLBYFATHER(String provinceId);
}
