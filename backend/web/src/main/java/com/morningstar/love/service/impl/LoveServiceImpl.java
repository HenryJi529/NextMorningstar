package com.morningstar.love.service.impl;

import com.morningstar.love.pojo.bo.LovePhotoData;
import com.morningstar.love.properties.LoveProperties;
import com.morningstar.love.service.LoveService;
import com.morningstar.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Random;

@Service("loveService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoveServiceImpl implements LoveService {

    private final LoveProperties loveProperties;

    @Override
    public LovePhotoData getRandomData() {
        int maxBlockLevel = 8;
        int minBlockWidth = 3;
        int minBlockHeight = 4;

        int boardWidth = (int) Math.pow(2, maxBlockLevel-1) * minBlockWidth;
        int boardHeight = (int) Math.pow(2, maxBlockLevel-1) * minBlockHeight;

        String photoName = loveProperties.getPhotos().get(new Random().nextInt(loveProperties.getPhotos().size()));
        String resourcePath = "media/love/photo/" + photoName;

        BufferedImage originalImage = ImageUtil.readFromResource(resourcePath);
        BufferedImage resizedImage = ImageUtil.resize(originalImage, boardWidth, boardHeight);

        String[][] fills = new String[boardHeight / minBlockHeight][boardWidth / minBlockWidth];
        int[][][] pixels = ImageUtil.getRgbArray(resizedImage);
        for(int i = 0; i < fills.length; i++){
            for (int j = 0; j < fills[0].length; j++){
                int[] heightList = {i*minBlockHeight+1, i*minBlockHeight+2};
                int[] widthList = {j*minBlockWidth+1, j*minBlockWidth+1};
                int r = 0, g = 0, b = 0;
                for(int k = 0; k < heightList.length; k++){
                    r += pixels[heightList[k]][widthList[k]][0];
                    g += pixels[heightList[k]][widthList[k]][1];
                    b += pixels[heightList[k]][widthList[k]][2];
                }
                fills[i][j] = "#" + Integer.toHexString(r/heightList.length) + Integer.toHexString(g/heightList.length) + Integer.toHexString(b/heightList.length);
            }
        }

        return LovePhotoData.builder()
                .maxBlockLevel(maxBlockLevel).minBlockWidth(minBlockWidth).minBlockHeight(minBlockHeight)
                .photoName(photoName).base64String(ImageUtil.getBase64String(resizedImage)).fills(fills).build();
    }
}
