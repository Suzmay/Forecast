package com.jnu.weather.dao;

import com.jnu.weather.po.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, String> {
    public List<Province> findAllByProvinceLike(String province);
} 