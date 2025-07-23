package com.jnu.weather.domain;

import java.io.Serializable;

public class WeatherResult implements Serializable {
    private String date;
    private String week;
    private String areaid;
    private String weather;
    private String weatherimg;
    private String province;
    private String area;
    private String weathercode;
    private String real;
    private String lowest;
    private String highest;
    private String wind;
    private String windspeed;
    private String windsc;
    private String sunrise;
    private String sunset;
    private String moonrise;
    private String moondown;
    private String pcpn;
    private String uv_index;
    private String aqi;
    private String quality;
    private String vis;
    private String humidity;
    private String tips;
    private String poem;
    private java.util.List<Alarm> alarmlist;
    private java.util.List<WeatherResult> list;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWeatherimg() {
        return weatherimg;
    }

    public void setWeatherimg(String weatherimg) {
        this.weatherimg = weatherimg;
    }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getWeathercode() { return weathercode; }
    public void setWeathercode(String weathercode) { this.weathercode = weathercode; }
    public String getReal() { return real; }
    public void setReal(String real) { this.real = real; }
    public String getLowest() { return lowest; }
    public void setLowest(String lowest) { this.lowest = lowest; }
    public String getHighest() { return highest; }
    public void setHighest(String highest) { this.highest = highest; }
    public String getWind() { return wind; }
    public void setWind(String wind) { this.wind = wind; }
    public String getWindspeed() { return windspeed; }
    public void setWindspeed(String windspeed) { this.windspeed = windspeed; }
    public String getWindsc() { return windsc; }
    public void setWindsc(String windsc) { this.windsc = windsc; }
    public String getSunrise() { return sunrise; }
    public void setSunrise(String sunrise) { this.sunrise = sunrise; }
    public String getSunset() { return sunset; }
    public void setSunset(String sunset) { this.sunset = sunset; }
    public String getMoonrise() { return moonrise; }
    public void setMoonrise(String moonrise) { this.moonrise = moonrise; }
    public String getMoondown() { return moondown; }
    public void setMoondown(String moondown) { this.moondown = moondown; }
    public String getPcpn() { return pcpn; }
    public void setPcpn(String pcpn) { this.pcpn = pcpn; }
    public String getUv_index() { return uv_index; }
    public void setUv_index(String uv_index) { this.uv_index = uv_index; }
    public String getAqi() { return aqi; }
    public void setAqi(String aqi) { this.aqi = aqi; }
    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }
    public String getVis() { return vis; }
    public void setVis(String vis) { this.vis = vis; }
    public String getHumidity() { return humidity; }
    public void setHumidity(String humidity) { this.humidity = humidity; }
    public String getTips() { return tips; }
    public void setTips(String tips) { this.tips = tips; }
    public String getPoem() {
        return poem;
    }
    public void setPoem(String poem) {
        this.poem = poem;
    }
    public java.util.List<Alarm> getAlarmlist() { return alarmlist; }
    public void setAlarmlist(java.util.List<Alarm> alarmlist) { this.alarmlist = alarmlist; }
    public java.util.List<WeatherResult> getList() {
        return list;
    }

    public void setList(java.util.List<WeatherResult> list) {
        this.list = list;
    }
}
