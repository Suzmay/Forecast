package com.jnu.weather.domain;

import java.io.Serializable;

public class WeatherResponse implements Serializable {
    private Integer code;
    private String msg;

    private WeatherResult result;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public WeatherResult getResult() {
        return result;
    }

    public void setResult(WeatherResult result) {
        this.result = result;
    }
}
