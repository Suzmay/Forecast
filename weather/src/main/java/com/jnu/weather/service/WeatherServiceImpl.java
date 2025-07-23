package com.jnu.weather.service;

import com.jnu.weather.domain.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RestTemplate restTemplate;

    public WeatherResponse accessThree(String city){
        String url="https://apis.tianapi.com/tianqi/index?key="+"1066d2238af963b2fb85e643abbcffb1"+"&city=" + city + "&type=1";
        return restTemplate.getForObject(url, WeatherResponse.class);
    }

    // 天气类型映射到tqtype
    private int mapWeatherToTqType(String weather) {
        if (weather == null) return 0;
        if (weather.contains("风")) return 1;
        if (weather.contains("云")) return 2;
        if (weather.contains("雨")) return 3;
        if (weather.contains("雪")) return 4;
        if (weather.contains("霜")) return 5;
        if (weather.contains("露")) return 6;
        if (weather.contains("雾")) return 7;
        if (weather.contains("雷")) return 8;
        if (weather.contains("晴")) return 9;
        if (weather.contains("阴")) return 10;
        return 0;
    }
    // 调用诗句API
    private String fetchPoemByWeatherType(int tqtype) {
        String url = "https://apis.tianapi.com/tianqishiju/index?key=1066d2238af963b2fb85e643abbcffb1";
        if (tqtype > 0) url += "&tqtype=" + tqtype;
        try {
            org.json.JSONObject json = new org.json.JSONObject(restTemplate.getForObject(url, String.class));
            if (json.has("result")) {
                org.json.JSONObject result = json.getJSONObject("result");
                String content = result.has("content") ? result.getString("content") : "";
                String author = result.has("author") ? result.getString("author") : "";
                String source = result.has("source") ? result.getString("source") : "";
                String poem = content;
                if (!author.isEmpty() && !source.isEmpty()) {
                    poem += " ——" + author + "《" + source + "》";
                } else if (!author.isEmpty()) {
                    poem += " ——" + author;
                } else if (!source.isEmpty()) {
                    poem += " 《" + source + "》";
                }
                return poem;
            }
        } catch (Exception e) {
            System.out.println("[Service] 获取诗句失败: " + e.getMessage());
        }
        return "";
    }

    @Override
    public WeatherResponse accessThreeWithRedis(String city, String type){
        System.out.println("[Service] 收到city参数: " + city + ", type参数: " + type);
        String url="https://apis.tianapi.com/tianqi/index?key="+"1066d2238af963b2fb85e643abbcffb1"+"&city=" + city + "&type=" + type;
        WeatherResponse weatherResponse=(WeatherResponse) redisTemplate.opsForHash().get("weatherData", city+":"+type);
        if (weatherResponse==null){
            weatherResponse=restTemplate.getForObject(url, WeatherResponse.class);
            // 设置诗句
            if (weatherResponse != null && weatherResponse.getResult() != null) {
                if ("7".equals(type) && weatherResponse.getResult().getList() != null) {
                    for (com.jnu.weather.domain.WeatherResult day : weatherResponse.getResult().getList()) {
                        int tqtype = mapWeatherToTqType(day.getWeather());
                        day.setPoem(fetchPoemByWeatherType(tqtype));
                    }
                } else {
                    int tqtype = mapWeatherToTqType(weatherResponse.getResult().getWeather());
                    weatherResponse.getResult().setPoem(fetchPoemByWeatherType(tqtype));
                }
            }
            System.out.println("[Service] API返回内容: " + weatherResponse);
            redisTemplate.opsForHash().put("weatherData", city+":"+type, weatherResponse);
        } else {
            System.out.println("[Service] 命中缓存: " + weatherResponse);
        }
        return weatherResponse;
    }

    @Override
    public WeatherResponse accessThreeWithSnow(String city, String type){
        String url="https://apis.tianapi.com/tianqi/index?key="+"1066d2238af963b2fb85e643abbcffb1"+"&city=" + city + "&type=" + type;
        WeatherResponse weatherResponse=(WeatherResponse) redisTemplate.opsForHash().get("weatherData", city+":"+type);
        if (weatherResponse==null){
            synchronized (this){
                weatherResponse=(WeatherResponse) redisTemplate.opsForHash().get("weatherData", city+":"+type);
                if (weatherResponse==null){
                    weatherResponse=restTemplate.getForObject(url, WeatherResponse.class);
                    redisTemplate.opsForHash().put("weatherData", city+":"+type,weatherResponse);
                }
            }
        }
        return weatherResponse;
    }

    @Override
    public void cacheWeatherData(String city) {
        WeatherResponse weatherResponse=accessThree(city);
        redisTemplate.opsForHash().put("weatherData", city, weatherResponse);
    }
}
