package com.morningstar.love.service.impl;

import com.morningstar.exception.BaseException;
import com.morningstar.love.pojo.bo.LovePhotoData;
import com.morningstar.love.service.LoveService;
import com.morningstar.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoveServiceImpl implements LoveService {

    private final WebApplicationContext webApplicationContext;

    private final String rootPath = "media/love/photo/";

    private String getRandomFileName() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(webApplicationContext);
        try {
            Resource[] resources = resolver.getResources("classpath:" + rootPath + "*");
            Resource randomResource = resources[new Random().nextInt(resources.length)];
            return randomResource.getFilename();
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public String getSelectedData(String photoName) {
        String resourcePath = rootPath + photoName;
        BufferedImage originalImage = ImageUtil.read(new ClassPathResource(resourcePath));
        return ImageUtil.getBase64StringFromBufferedImage(originalImage);
    }

    @Override
    public LovePhotoData getRandomData() {
        String photoName = getRandomFileName();
        String resourcePath = rootPath + photoName;
        BufferedImage originalImage = ImageUtil.read(new ClassPathResource(resourcePath));

        int minBlockWidth;
        int minBlockHeight;
        int maxBlockLevel;
        if(originalImage.getHeight() > originalImage.getWidth()){
            if(Math.abs((1.0*originalImage.getHeight()/originalImage.getWidth() - 1.0*16/9)) < 1e-2){
                minBlockWidth = 9;
                minBlockHeight = 16;
                maxBlockLevel = 6;
            }else{
                minBlockWidth = 3;
                minBlockHeight = 4;
                maxBlockLevel = 8;
            }
        }else{
            if(Math.abs((1.0*originalImage.getHeight()/originalImage.getWidth() - 1.0*9/16)) < 1e-2){
                minBlockWidth = 16;
                minBlockHeight = 9;
                maxBlockLevel = 6;
            }else{
                minBlockWidth = 4;
                minBlockHeight = 3;
                maxBlockLevel = 8;
            }
        }

        int perRowColumnNum = (int) Math.pow(2, maxBlockLevel - 1);
        int boardWidth = perRowColumnNum * minBlockWidth;
        int boardHeight = perRowColumnNum * minBlockHeight;
        BufferedImage resizedImage = ImageUtil.resize(originalImage, boardWidth, boardHeight);

        String[][] fills = new String[perRowColumnNum][perRowColumnNum];
        int[][][] pixels = ImageUtil.getRgbArray(resizedImage);

        for (int i = 0; i < fills.length; i++) {
            for (int j = 0; j < fills[0].length; j++) {
                // 选择需要平均的像素点
                int[] heightList = {i * minBlockHeight + 1, i * minBlockHeight + 2};
                int[] widthList = {j * minBlockWidth + 1, j * minBlockWidth + 1};
                int r = 0, g = 0, b = 0;
                for (int k = 0; k < heightList.length; k++) {
                    r += pixels[heightList[k]][widthList[k]][0];
                    g += pixels[heightList[k]][widthList[k]][1];
                    b += pixels[heightList[k]][widthList[k]][2];
                }
                String rString = Integer.toHexString(r / heightList.length);
                String gString = Integer.toHexString(g / heightList.length);
                String bString = Integer.toHexString(b / heightList.length);
                if (rString.length() == 1) {
                    rString = "0" + rString;
                }
                if (gString.length() == 1) {
                    gString = "0" + gString;
                }
                if (bString.length() == 1) {
                    bString = "0" + bString;
                }
                fills[i][j] = "#" + rString + gString + bString;
            }
        }

        return LovePhotoData
                .builder()
                .maxBlockLevel(maxBlockLevel)
                .minBlockWidth(minBlockWidth)
                .minBlockHeight(minBlockHeight)
                .photoName(photoName)
                .base64String(ImageUtil.getBase64StringFromBufferedImage(resizedImage))
                .fills(fills)
                .build();
    }
}
