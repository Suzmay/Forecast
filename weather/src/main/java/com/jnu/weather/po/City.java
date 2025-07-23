package com.jnu.weather.po;

import javax.persistence.*;

@Entity
@Table(name="tab_city")
public class City {

    @Id
    @Column(name="cityid")
    private String cityId;
    
    @Column(name="city")
    private String city;
    @Column(name = "father")
    private String father;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }
}
