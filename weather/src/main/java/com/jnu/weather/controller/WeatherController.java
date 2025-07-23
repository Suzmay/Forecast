package com.jnu.weather.controller;

import com.jnu.weather.domain.WeatherResponse;
import com.jnu.weather.po.City;
import com.jnu.weather.po.Province;
import com.jnu.weather.service.CityService;
import com.jnu.weather.service.WeatherService;
import com.jnu.weather.dao.ProvinceRepository;
import com.jnu.weather.dao.CityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
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
    CityService cityService;
    @Autowired
    ProvinceRepository provinceRepository;
    @Autowired
    CityRepository cityRepository;

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


    @GetMapping("/getWeatherThy")
    public ModelAndView getWeatherByCity(Model model, @RequestParam(value = "city", required = false) String city, @RequestParam(value = "type", required = false, defaultValue = "") String type) {
        List<City> cityList = cityService.findAllCity();
        model.addAttribute("cityList", cityList);
        model.addAttribute("selectedCityId", city);
        model.addAttribute("selectedType", type);

        // 省份列表
        List<Province> provinceList = provinceRepository.findAll();
        model.addAttribute("provinceList", provinceList);

        // 省份-城市映射
        // 先查出所有城市，按father分组
        List<City> allCities = cityRepository.findAll();
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
            List<City> cities = new ArrayList<>();
            for (City c : allCities) {
                if (pid.equals(c.getFather())) {
                    cities.add(c);
                }
            }
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

        // 新增：查找城市名称
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
            ObjectMapper objectMapper = new ObjectMapper();
            model.addAttribute("weatherdataJson", weatherdata == null ? null : objectMapper.writeValueAsString(weatherdata));
        } catch (Exception e) {
            model.addAttribute("weatherdataJson", null);
        }
        return new ModelAndView("forecast");
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
}

