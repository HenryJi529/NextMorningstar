package com.morningstar.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.morningstar.exception.BaseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.regex.Pattern;

public class ImageUtil {
    public static boolean isValidImageFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        String regex = "(?i)[^\\\\/:*?\"<>|\\r\\n]+\\.(jpg|jpeg|png|gif|bmp|webp|svg|tiff|tif|ico|heic|heif|avif|apng|jfif|pjpeg|pjp)$";
        return Pattern.matches(regex, filename);
    }

    public static BufferedImage read(ClassPathResource classPathResource) {
        int rotateTimes = getRotate90CWTimes(classPathResource);
        BufferedImage image = readPixelData(classPathResource);
        while (rotateTimes > 0) {
            image = rotate90CW(image);
            rotateTimes--;
        }
        return image;
    }

    public static BufferedImage read(MultipartFile multipartFile) {
        try {
            int rotateTimes = getRotate90CWTimes(multipartFile.getInputStream());
            BufferedImage image = readPixelData(multipartFile.getInputStream());
            while (rotateTimes > 0) {
                image = rotate90CW(image);
                rotateTimes--;
            }
            return image;
        } catch (IOException e) {
            throw new BaseException("IO错误: " + e.getMessage());
        }
    }

    private static BufferedImage readPixelData(InputStream inputStream) {
        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new BaseException("图片读取失败: " + e.getMessage());
        }
    }

    private static BufferedImage readPixelData(ClassPathResource classPathResource) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(classPathResource.getPath())) {
            return readPixelData(inputStream);
        } catch (IOException e) {
            throw new BaseException("图片读取失败: " + e.getMessage());
        }
    }

    private static Metadata readMetaData(ClassPathResource classPathResource) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(classPathResource.getPath())) {
            return ImageUtil.readMetaData(inputStream);
        } catch (IOException e) {
            throw new BaseException("图片读取失败: " + e.getMessage());
        }
    }

    private static Metadata readMetaData(InputStream inputStream) {
        Metadata metadata;
        try {
            metadata = ImageMetadataReader.readMetadata(inputStream);
        } catch (ImageProcessingException e) {
            throw new BaseException("图片处理错误: " + e.getMessage());
        } catch (IOException e) {
            throw new BaseException("读取图片资源时发生IO错误：" + e.getMessage());
        }
        return metadata;
    }

    private static Integer getRotate90CWTimes(ClassPathResource classPathResource) {
        Metadata metadata = readMetaData(classPathResource);
        return getRotate90CWTimes(metadata);
    }

    private static Integer getRotate90CWTimes(InputStream inputStream) {
        Metadata metadata = readMetaData(inputStream);
        return getRotate90CWTimes(metadata);
    }

    private static Integer getRotate90CWTimes(Metadata metadata) {
        String description = "";
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                if (tag.getTagType() == ExifDirectoryBase.TAG_ORIENTATION) {
                    description = tag.getDescription();
                }
            }
        }
        if (description.equals("Right side, top (Rotate 90 CW)")) {
            return 1;
        } else {
            return 0;
        }
    }

    private static BufferedImage rotate90CW(BufferedImage originalImage) {
        int newHeight = originalImage.getWidth();
        int newWidth = originalImage.getHeight();

        // 创建一个新的 BufferedImage
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // 创建 Graphics2D 对象
        Graphics2D g2d = rotatedImage.createGraphics();

        // 进行旋转
        g2d.translate(newWidth / 2, newHeight / 2);
        g2d.rotate(Math.PI / 2);
        g2d.drawImage(originalImage, -newHeight / 2, -newWidth / 2, null);

        // 在旋转后的画布上绘制原图像
        g2d.drawImage(originalImage, 0, -newHeight, null);

        // 释放资源
        g2d.dispose();

        return rotatedImage;
    }

    public static BufferedImage resize(BufferedImage originalImage, int newWidth, int newHeight) {
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // 绘制缩放后的图片
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return scaledImage;
    }

    public static String getBase64StringFromBufferedImage(BufferedImage image) {
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
            // 将BufferedImage写入到字节输出流
            ImageIO.write(image, "png", bao);
            // 获取字节数据
            byte[] imageBytes = bao.toByteArray();
            // 使用Base64编码器编码字节数据
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new BaseException("字节流读取错误: " + e.getMessage());
        }
    }

    public static String getPureBase64StringFromDataBase64String(String dataBase64String) {
        int base64Index = dataBase64String.indexOf("base64,");
        if (base64Index == -1) {
            throw new IllegalArgumentException("无效的Data URL格式，缺少base64前缀");
        }
        return dataBase64String.substring(base64Index + 7);
    }

    public static BufferedImage getBufferedImageFromBase64String(String base64String) {
        if (base64String == null || !base64String.matches("^data:image/\\w+;base64,[a-zA-Z0-9+/]+={0,2}$")) {
            throw new BaseException("不是合法的Base64字符串");
        }

        // 提取纯Base64
        String pureBase64String = getPureBase64StringFromDataBase64String(base64String);

        // 解码Base64
        byte[] imageBytes = Base64.getDecoder().decode(pureBase64String);

        // 转换为BufferedImage
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(bais);
            if (image == null) {
                throw new BaseException("无法解码图片数据，可能格式不受支持或数据已损坏");
            }
            return image;
        } catch (IOException e) {
            throw new BaseException("IO错误: " + e.getMessage());
        }
    }

    public static int[][][] getRgbArray(BufferedImage image) {
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
