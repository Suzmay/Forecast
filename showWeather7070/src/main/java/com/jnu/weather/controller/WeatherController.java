package com.jnu.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnu.weather.client.CityServiceClient;
import com.jnu.weather.client.ProvinceServiceClient;
import com.jnu.weather.domain.WeatherResponse;
import com.jnu.weather.po.City;
import com.jnu.weather.po.Province;
import com.jnu.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Controller
public class WeatherController {
    ThymeleafProperties thymeleafProperties;

    @Autowired
    WeatherService weatherService;
    @Autowired
    CityServiceClient cityServiceClient;
    @Autowired
    ProvinceServiceClient provinceServiceClient;
    @Autowired
    org.springframework.data.redis.core.RedisTemplate redisTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, String> weatherIconMap = new HashMap<>();
    static {
        weatherIconMap.put("阴", "yin");
        weatherIconMap.put("晴", "qing");
        weatherIconMap.put("多云", "duoyun");
        weatherIconMap.put("暴雨", "baoyu");
        weatherIconMap.put("大雨", "dayu");
        weatherIconMap.put("中雨", "zhongyu");
        weatherIconMap.put("小雨", "xiaoyu");
        weatherIconMap.put("阵雨", "zhenyu");
        weatherIconMap.put("雨", "yu");
        weatherIconMap.put("雷阵雨", "leizhenyu");
        weatherIconMap.put("大暴雨", "dabaoyu");
        weatherIconMap.put("特大暴雨", "tedabaoyu");
        weatherIconMap.put("冻雨", "dongyu");
        weatherIconMap.put("暴雪", "baoxue");
        weatherIconMap.put("大雪", "daxue");
        weatherIconMap.put("中雪", "zhongxue");
        weatherIconMap.put("小雪", "xiaoxue");
        weatherIconMap.put("雨夹雪", "yujiaxue");
        weatherIconMap.put("雪", "xue");
        weatherIconMap.put("冰雹", "bingbao");
        weatherIconMap.put("浮尘", "fuchen");
        weatherIconMap.put("扬沙", "yangsha");
        weatherIconMap.put("沙尘暴", "shachenbao");
        weatherIconMap.put("大雾", "dawu");
        weatherIconMap.put("雾", "wu");
        weatherIconMap.put("霾", "mai");
    }

    @RequestMapping("/getWeatherThy")
    public ModelAndView getWeatherByCity(Model model, @RequestParam(value = "city", required = false) String city, @RequestParam(value = "type", required = false, defaultValue = "") String type) {
        // 检查并清除过期缓存
        clearExpiredCache();
        
        try {
            // 使用Feign客户端调用城市服务
            List<City> cityList = cityServiceClient.findAllCities();
            model.addAttribute("cityList", cityList);
            model.addAttribute("selectedCityId", city);
            model.addAttribute("selectedType", type);

            // 使用Feign客户端调用省份服务
            List<Province> provinceList = provinceServiceClient.findAllProvinces();
            model.addAttribute("provinceList", provinceList);

            // 省份-城市映射
            List<City> allCities = cityServiceClient.findAllCities();
            System.out.println("[DEBUG] allCities size: " + allCities.size());
            Set<String> uniqueCityIds = new HashSet<>();
            for (City c : allCities) {
                if (!uniqueCityIds.add(c.getCityId())) {
                    System.out.println("[DUPLICATE] cityId: " + c.getCityId() + " city: " + c.getCity() + " father: " + c.getFather());
                }
            }
            System.out.println("[DEBUG] unique cityid count: " + uniqueCityIds.size());
            Map<String, List<City>> provinceCityMap = new HashMap<>();
            for (Province p : provinceList) {
                String pid = p.getProvinceId();
                List<City> cities = cityServiceClient.findCitiesByProvince(pid);
                System.out.println("[DEBUG] province " + pid + " city count: " + cities.size());
                provinceCityMap.put(pid, cities);
            }
            System.out.println("[DEBUG] provinceCityMap keys: " + provinceCityMap.keySet());
            try {
                model.addAttribute("provinceCityMap", provinceCityMap);
                model.addAttribute("provinceListData", provinceList);
            } catch (Exception e) {
                model.addAttribute("provinceCityMap", "{}");
                model.addAttribute("provinceListJson", "[]");
            }

            // 查找城市名称
            String selectedCityName = "";
            if (city != null && !city.trim().isEmpty()) {
                for (City c : cityList) {
                    if (c.getCityId().equals(city)) {
                        selectedCityName = c.getCity();
                        break;
                    }
                }
            }
            model.addAttribute("selectedCityName", selectedCityName);

            System.out.println("[Controller] 收到city参数: " + city);
            System.out.println("[Controller] 收到type参数: " + type);

            WeatherResponse weatherdata = null;
            if (city != null && !city.trim().isEmpty()) {
                weatherdata = weatherService.accessThreeWithRedis(city, type);
                System.out.println("[Controller] 获取到天气数据: " + weatherdata);
                if (weatherdata != null) {
                    System.out.println("[Controller] weatherdata不为null");
                    if (weatherdata.getResult() != null) {
                        System.out.println("[Controller] Result不为null: " + weatherdata.getResult());
                        if ("7".equals(type)) {
                            System.out.println("[Controller] 7天查询 - list字段: " + weatherdata.getResult().getList());
                            if (weatherdata.getResult().getList() != null) {
                                System.out.println("[Controller] 7天查询 - list大小: " + weatherdata.getResult().getList().size());
                            }
                        }
                    } else {
                        System.out.println("[Controller] Result为null");
                    }
                } else {
                    System.out.println("[Controller] weatherdata为null");
                }
                model.addAttribute("weatherdata", weatherdata);

                // 根据天气描述确定本地图标路径和gif拼音
                if (weatherdata != null && weatherdata.getResult() != null && weatherdata.getResult().getWeather() != null) {
                    String weatherDescription = weatherdata.getResult().getWeather();
                    String iconName = weatherIconMap.get(weatherDescription);
                    if (iconName != null) {
                        String iconPath = "/images/png/" + iconName + ".png";
                        model.addAttribute("weatherIconPath", iconPath);
                        model.addAttribute("weatherGifPinyin", iconName); // 新增gif拼音变量
                        System.out.println("[DEBUG] 视频/图标路径: iconName=" + iconName + ", iconPath=" + iconPath);
                    }
                }
            }
            try {
                String weatherdataJson = weatherdata == null ? null : objectMapper.writeValueAsString(weatherdata);
                System.out.println("[Controller] weatherdataJson: " + weatherdataJson);
                model.addAttribute("weatherdataJson", weatherdataJson);
            } catch (Exception e) {
                System.out.println("[Controller] JSON序列化失败: " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("weatherdataJson", null);
            }
            return new ModelAndView("forecast","cityList",cityList);
        } catch (Exception e) {
            System.out.println("[ERROR] 调用省份或城市服务失败: " + e.getMessage());
            e.printStackTrace();
            // 返回错误页面或默认数据
            return new ModelAndView("error");
        }
    }

    @PostMapping("/api/ai-analyze")
    @ResponseBody
    public ResponseEntity<?> aiAnalyze(@RequestBody Map<String, String> body) {
        String weatherInfo = body.getOrDefault("weatherInfo", "");
        if (weatherInfo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "weatherInfo不能为空"));
        }
        try {
            // 切换为讯飞星火大模型X1 API
            String apiKey = "dkOEBlPDXVdvIIgSoOmI:AYtDdbhSYzqHILatwWYK";
            String apiUrl = "https://spark-api-open.xf-yun.com/v2/chat/completions";
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "x1");
            payload.put("user", "weather-user");
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "你是一个专业的天气分析助手。"));
            messages.add(Map.of("role", "user", "content", weatherInfo));
            payload.put("messages", messages);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Map resp = mapper.readValue(response.body(), Map.class);
                List choices = (List) resp.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map choice = (Map) choices.get(0);
                    Map message = (Map) choice.get("message");
                    String content = (String) message.get("content");
                    return ResponseEntity.ok(Map.of("result", content));
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "AI分析失败", "detail", response.body()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "AI分析异常", "detail", e.getMessage()));
        }
    }
    
    /**
     * 手动清除所有天气缓存（用于调试）
     */
    @PostMapping("/api/clear-cache")
    @ResponseBody
    public ResponseEntity<?> clearAllCache() {
        try {
            System.out.println("[Cache] 手动清除所有缓存...");
            redisTemplate.delete("weatherData");
            System.out.println("[Cache] 所有缓存已清除");
            return ResponseEntity.ok(Map.of("message", "缓存清除成功"));
        } catch (Exception e) {
            System.out.println("[Cache] 清除缓存失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "清除缓存失败", "detail", e.getMessage()));
        }
    }
    
    /**
     * 检查并清除过期缓存
     */
    private void clearExpiredCache() {
        try {
            System.out.println("[Cache] 开始检查过期缓存...");
            
            // 获取所有缓存的天气数据
            Map<Object, Object> allWeatherData = redisTemplate.opsForHash().entries("weatherData");
            System.out.println("[Cache] 当前缓存数量: " + allWeatherData.size());
            
            if (allWeatherData.isEmpty()) {
                System.out.println("[Cache] 没有缓存数据，跳过清理");
                return;
            }
            
            int expiredCount = 0;
            int validCount = 0;
            
            for (Map.Entry<Object, Object> entry : allWeatherData.entrySet()) {
                String cacheKey = (String) entry.getKey();
                WeatherResponse weatherResponse = (WeatherResponse) entry.getValue();
                
                // 检查数据是否有效
                if (weatherResponse == null || weatherResponse.getResult() == null) {
                    System.out.println("[Cache] 发现无效数据，删除缓存: " + cacheKey);
                    redisTemplate.opsForHash().delete("weatherData", cacheKey);
                    expiredCount++;
                    continue;
                }
                
                // 检查数据完整性
                boolean isValid = true;
                String[] keyParts = cacheKey.split(":");
                if (keyParts.length == 2) {
                    String city = keyParts[0];
                    String type = keyParts[1];
                    
                    // 检查7天天气数据的list字段
                    if ("7".equals(type) && weatherResponse.getResult().getList() == null) {
                        System.out.println("[Cache] 发现不完整的7天天气数据，删除缓存: " + cacheKey);
                        isValid = false;
                    }
                    
                    // 检查基本字段
                    if (weatherResponse.getResult().getWeather() == null || 
                        weatherResponse.getResult().getDate() == null) {
                        System.out.println("[Cache] 发现缺少基本字段的数据，删除缓存: " + cacheKey);
                        isValid = false;
                    }
                    
                    // 检查数据日期是否过期
                    if (weatherResponse.getResult().getDate() != null) {
                        try {
                            java.time.LocalDate cacheDate = java.time.LocalDate.parse(weatherResponse.getResult().getDate());
                            java.time.LocalDate currentDate = java.time.LocalDate.now();
                            
                            // 如果缓存日期不是今天，且不是未来日期，则认为是过期数据
                            if (!cacheDate.equals(currentDate) && cacheDate.isBefore(currentDate)) {
                                System.out.println("[Cache] 发现过期数据，缓存日期: " + cacheDate + ", 当前日期: " + currentDate + ", 删除缓存: " + cacheKey);
                                isValid = false;
                            }
                        } catch (Exception e) {
                            System.out.println("[Cache] 解析日期失败: " + weatherResponse.getResult().getDate() + ", 删除缓存: " + cacheKey);
                            isValid = false;
                        }
                    }
                }
                
                if (!isValid) {
                    redisTemplate.opsForHash().delete("weatherData", cacheKey);
                    expiredCount++;
                } else {
                    validCount++;
                }
            }
            
            System.out.println("[Cache] 缓存清理完成 - 有效: " + validCount + ", 删除: " + expiredCount);
            
        } catch (Exception e) {
            System.out.println("[Cache] 清理缓存时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

