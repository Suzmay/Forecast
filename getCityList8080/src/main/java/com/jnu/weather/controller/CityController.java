package com.jnu.weather.controller;

import com.jnu.weather.po.City;
import com.jnu.weather.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city")
public class CityController {
    
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        System.out.println("[DEBUG] 城市服务健康检查接口被调用");
        return "City Service is running!";
    }
    
    @Autowired
    private CityService cityService;

    @GetMapping("/FINDALL")
    @ResponseBody
    public List<City> FINDALLCITY() {
        System.out.println("[DEBUG] CityController.FINDALLCITY() 被调用");
        try {
            List<City> result = cityService.FINDALLCITY();
            System.out.println("[DEBUG] 查询到 " + result.size() + " 个城市");
            if (result.isEmpty()) {
                System.out.println("[WARNING] 城市列表为空，请检查数据库连接和表数据");
                System.out.println("[DEBUG] 数据库连接: weather数据库");
                System.out.println("[DEBUG] 表名: tab_city");
            } else {
                System.out.println("[DEBUG] 第一个城市: " + result.get(0).getCity());
            }
            return result;
        } catch (Exception e) {
            System.out.println("[ERROR] 查询城市失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/FINDALLBYCITYLIKE")
    @ResponseBody
    public List<City> FINDALLBYCITYLIKE(@RequestParam String city) {
        return cityService.FINDALLBYCITYLIKE(city);
    }

    @GetMapping("/FINDALLBYFATHER")
    @ResponseBody
    public List<City> FINDALLBYFATHER(@RequestParam String provinceId) {
        return cityService.FINDALLBYFATHER(provinceId);
    }
}
