package com.jnu.weather.service;

import com.jnu.weather.po.Province;
import java.util.List;

public interface ProvinceService {
    List<Province> FINDALLPROVINCE();
    List<Province> FINDALLBYPROVINCELIKE(String province);
}
