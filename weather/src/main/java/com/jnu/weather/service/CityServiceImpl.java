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

    // 查询城市列表数据
    @Override
    public List<City> findAllCity() {
        return cityRepository.findAll();
    }
}
