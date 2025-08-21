package com.jnu.weather.controller;

import com.jnu.weather.po.Province;
import com.jnu.weather.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/province")
public class ProvinceController {
    
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        System.out.println("[DEBUG] 健康检查接口被调用");
        return "Province Service is running!";
    }
    
    @Autowired
    private ProvinceService provinceService;

    @GetMapping("/FINDALL")
    @ResponseBody
    public List<Province> FINDALLPROVINCE() {
        System.out.println("[DEBUG] ProvinceController.FINDALLPROVINCE() 被调用");
        try {
            List<Province> result = provinceService.FINDALLPROVINCE();
            System.out.println("[DEBUG] 查询到 " + result.size() + " 个省份");
            return result;
        } catch (Exception e) {
            System.out.println("[ERROR] 查询省份失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/FINDALLBYPROVINCELIKE")
    @ResponseBody
    public List<Province> FINDALLBYPROVINCELIKE(@RequestParam String province) {
        return provinceService.FINDALLBYPROVINCELIKE(province);
    }
}
