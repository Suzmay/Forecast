package com.jnu.weather.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jnu.weather.po.City;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City,String> {
    public List<City> findAllByCityLike(String city);
    public List<City> findAllByFather(String provinceId);
}
