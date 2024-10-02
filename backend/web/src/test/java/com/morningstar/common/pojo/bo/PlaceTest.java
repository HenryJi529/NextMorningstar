package com.morningstar.common.pojo.bo;

import org.junit.jupiter.api.Test;

public class PlaceTest {
    @Test
    public void testGetNearestPlace() {
        double latitude = 32;
        double longitude = 118; // 南京的经纬度
        System.out.println(Place.getNearestPlace(latitude, longitude));
    }
}
