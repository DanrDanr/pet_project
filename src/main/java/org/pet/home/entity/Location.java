package org.pet.home.entity;

import lombok.Data;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
@Data
public class Location {
    private String formattedAddress;
    private double longitude;//经度
    private double latitude;//维度
    public Location(String formattedAddress,double longitude, double latitude) {
        this.formattedAddress =formattedAddress;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
