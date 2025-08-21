package com.jnu.weather.service;

import com.jnu.weather.dao.CityRepository;
import com.jnu.weather.po.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {
    @Autowired
    CityRepository cityRepository;

    @Override
    public List<City> FINDALLCITY() {
        return cityRepository.findAll();
    }

    @Override
    public List<City> FINDALLBYCITYLIKE(String city) {
        return cityRepository.findAllByCityLike(city);
    }

    @Override
    public List<City> FINDALLBYFATHER(String provinceId) {
        return cityRepository.findAllByFather(provinceId);
    }
}
