package org.pet.home.entity;

import lombok.Data;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
@Data
public class AddressInfo {
    private String formattedAddress;
    private String country;
    private String province;
    private String cityCode;
    private String city;
    private String district;
    private String adCode;
    private String location;


    public AddressInfo(String formattedAddress, String country, String province, String cityCode, String city, String district, String adCode, String location) {
        this.formattedAddress = formattedAddress;
        this.country = country;
        this.province = province;
        this.cityCode = cityCode;
        this.city = city;
        this.district = district;
        this.adCode = adCode;
        this.location = location;
    }

}
