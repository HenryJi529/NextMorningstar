package com.morningstar.kill.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;

public class ImageUtil {
    public static BufferedImage readFromResource(String resourcePath) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            // 使用ImageIO读取BufferedImage
            return ImageIO.read(Objects.requireNonNull(inputStream));
        } catch (IOException e) {
            throw new RuntimeException("Error reading image from resources", e);
        }
    }

    public static BufferedImage resize(BufferedImage originalImage, int newWidth, int newHeight){
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // 绘制缩放后的图片
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return scaledImage;
    }

    public static String getBase64String(BufferedImage image){
        try(ByteArrayOutputStream bao = new ByteArrayOutputStream()){
            // 将BufferedImage写入到字节输出流
            ImageIO.write(image, "png", bao);
            // 获取字节数据
            byte[] imageBytes = bao.toByteArray();
            // 使用Base64编码器编码字节数据
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int[][][] getRgbArray(BufferedImage image){
        // 获取图像的宽度和高度
        int width = image.getWidth();
        int height = image.getHeight();

        // 创建三维数组，这里假设我们只处理RGB三个通道
        int[][][] pixels = new int[height][width][3];

        // 遍历图像的每个像素
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 获取像素的颜色值
                int rgb = image.getRGB(x, y);
                // 将颜色值分解为RGB三个通道，并存储到数组中
                pixels[y][x][0] = (rgb >> 16) & 0xFF; // Red component
                pixels[y][x][1] = (rgb >> 8) & 0xFF;  // Green component
                pixels[y][x][2] = rgb & 0xFF;         // Blue component
            }
        }

        return pixels;
    }
}
