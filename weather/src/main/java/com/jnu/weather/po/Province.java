package com.jnu.weather.po;

import javax.persistence.*;

@Entity
@Table(name="tab_province")
public class Province {
    @Id
    @Column(name="provinceid")
    private String provinceId;
    @Column(name="province")
    private String province;

    public String getProvinceId() { return provinceId; }
    public void setProvinceId(String provinceId) { this.provinceId = provinceId; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
} 