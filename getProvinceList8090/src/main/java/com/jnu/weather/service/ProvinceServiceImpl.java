package com.jnu.weather.service;

import com.jnu.weather.dao.ProvinceRepository;
import com.jnu.weather.po.Province;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceServiceImpl implements ProvinceService {
    @Autowired
    ProvinceRepository provinceRepository;

    @Override
    public List<Province> FINDALLPROVINCE() {
        return provinceRepository.findAll();
    }

    @Override
    public List<Province> FINDALLBYPROVINCELIKE(String province) {
        return provinceRepository.findAllByProvinceLike(province);
    }
}
