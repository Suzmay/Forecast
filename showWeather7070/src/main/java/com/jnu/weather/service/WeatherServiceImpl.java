package com.jnu.weather.service;

import com.jnu.weather.domain.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RestTemplate restTemplate;

    public WeatherResponse accessThree(String city){
        String url="https://apis.tianapi.com/tianqi/index?key="+"b885bb2877f309169fbdda5f0a3d9795"+"&city=" + city + "&type=1";
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
    
    // 根据天气数据类型获取缓存过期时间
    private Duration getCacheExpiration(String type) {
        switch (type) {
            case "1": // 实时天气 - 30分钟
                return Duration.ofMinutes(30);
            case "7": // 7天预报 - 2小时
                return Duration.ofHours(2);
            default: // 默认1小时
                return Duration.ofHours(1);
        }
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
        String cacheKey = city+":"+type;
        WeatherResponse weatherResponse=(WeatherResponse) redisTemplate.opsForHash().get("weatherData", cacheKey);
        
        if (weatherResponse==null){
            System.out.println("[Service] 缓存未命中，从API拉取数据: " + city + " (type: " + type + ")");
        } else {
            // 检查缓存数据是否过期（基于日期）
            if (weatherResponse.getResult() != null && weatherResponse.getResult().getDate() != null) {
                try {
                    java.time.LocalDate cacheDate = java.time.LocalDate.parse(weatherResponse.getResult().getDate());
                    java.time.LocalDate currentDate = java.time.LocalDate.now();
                    
                    // 如果缓存日期不是今天，且不是未来日期，则认为是过期数据
                    if (!cacheDate.equals(currentDate) && cacheDate.isBefore(currentDate)) {
                        System.out.println("[Service] 缓存数据已过期，缓存日期: " + cacheDate + ", 当前日期: " + currentDate + ", 重新从API拉取数据");
                        weatherResponse = null;
                        // 删除过期缓存
                        redisTemplate.opsForHash().delete("weatherData", cacheKey);
                    }
                } catch (Exception e) {
                    System.out.println("[Service] 解析缓存日期失败: " + weatherResponse.getResult().getDate() + ", 重新从API拉取数据");
                    weatherResponse = null;
                    // 删除无效缓存
                    redisTemplate.opsForHash().delete("weatherData", cacheKey);
                }
            }
        }
        
        if (weatherResponse==null){
            System.out.println("[Service] 缓存未命中或已过期，从API拉取数据: " + city + " (type: " + type + ")");
            String url="https://apis.tianapi.com/tianqi/index?key="+"b885bb2877f309169fbdda5f0a3d9795"+"&city=" + city + "&type=" + type;
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
            if (weatherResponse != null && weatherResponse.getResult() != null) {
                System.out.println("[Service] Result详情: " + weatherResponse.getResult());
                if ("7".equals(type)) {
                    System.out.println("[Service] 7天查询 - list字段: " + weatherResponse.getResult().getList());
                    if (weatherResponse.getResult().getList() != null) {
                        System.out.println("[Service] 7天查询 - list大小: " + weatherResponse.getResult().getList().size());
                    }
                }
            }
            // 缓存数据，根据类型设置不同的过期时间
            redisTemplate.opsForHash().put("weatherData", cacheKey, weatherResponse);
            Duration expiration = getCacheExpiration(type);
            System.out.println("[Service] 缓存数据，过期时间: " + expiration);
            try {
                redisTemplate.expire("weatherData", expiration);
            } catch (Exception e) {
                System.out.println("[Service] 设置缓存过期时间失败: " + e.getMessage());
            }
        } else {
            System.out.println("[Service] 命中缓存: " + city + ":" + type);
        }
        return weatherResponse;
    }

    @Override
    public WeatherResponse accessThreeWithSnow(String city, String type){
        String cacheKey = city+":"+type;
        WeatherResponse weatherResponse=(WeatherResponse) redisTemplate.opsForHash().get("weatherData", cacheKey);
        if (weatherResponse==null){
            synchronized (this){
                weatherResponse=(WeatherResponse) redisTemplate.opsForHash().get("weatherData", cacheKey);
                if (weatherResponse==null){
                } else {
                    // 检查缓存数据是否过期（基于日期）
                    if (weatherResponse.getResult() != null && weatherResponse.getResult().getDate() != null) {
                        try {
                            java.time.LocalDate cacheDate = java.time.LocalDate.parse(weatherResponse.getResult().getDate());
                            java.time.LocalDate currentDate = java.time.LocalDate.now();
                            
                            // 如果缓存日期不是今天，且不是未来日期，则认为是过期数据
                            if (!cacheDate.equals(currentDate) && cacheDate.isBefore(currentDate)) {
                                System.out.println("[Service] 缓存数据已过期，缓存日期: " + cacheDate + ", 当前日期: " + currentDate + ", 重新从API拉取数据");
                                weatherResponse = null;
                                // 删除过期缓存
                                redisTemplate.opsForHash().delete("weatherData", cacheKey);
                            }
                        } catch (Exception e) {
                            System.out.println("[Service] 解析缓存日期失败: " + weatherResponse.getResult().getDate() + ", 重新从API拉取数据");
                            weatherResponse = null;
                            // 删除无效缓存
                            redisTemplate.opsForHash().delete("weatherData", cacheKey);
                        }
                    }
                }
                
                if (weatherResponse==null){
                    System.out.println("[Service] 缓存未命中，从API拉取数据: " + city + " (type: " + type + ")");
                    String url="https://apis.tianapi.com/tianqi/index?key="+"1066d2238af963b2fb85e643abbcffb1"+"&city=" + city + "&type=" + type;
                    weatherResponse=restTemplate.getForObject(url, WeatherResponse.class);
                    redisTemplate.opsForHash().put("weatherData", cacheKey,weatherResponse);
                    // 根据类型设置不同的过期时间
                    Duration expiration = getCacheExpiration(type);
                    System.out.println("[Service] 缓存数据，过期时间: " + expiration);
                    try {
                        redisTemplate.expire("weatherData", expiration);
                    } catch (Exception e) {
                        System.out.println("[Service] 设置缓存过期时间失败: " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("[Service] 命中缓存: " + city + ":" + type);
        }
        return weatherResponse;
    }

    // 删除cacheWeatherData方法，改为按需加载策略
    // 只有用户查询时才从API拉取数据，避免浪费API配额
}
