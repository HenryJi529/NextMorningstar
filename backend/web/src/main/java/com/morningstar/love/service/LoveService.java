package com.morningstar.love.service;

import com.morningstar.love.pojo.bo.LovePhotoData;

public interface LoveService {
    String getSelectedData(String photoName);
    LovePhotoData getRandomData();
}
