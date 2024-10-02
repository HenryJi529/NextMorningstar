package com.morningstar.util;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageUtilTest {
    @Test
    public void testIsValidImageFilename() {
        assert ImageUtil.isValidImageFilename("test.png");
        assert !ImageUtil.isValidImageFilename("test.txt");
        assert !ImageUtil.isValidImageFilename("*test.jpeg");
        assert !ImageUtil.isValidImageFilename("a/test.jpeg");
    }

    @Test
    public void testReadFromClassPath() {
        System.out.println(Arrays.toString(ImageIO.getReaderFormatNames()));
        BufferedImage testPng1 = ImageUtil.read(new ClassPathResource("image/test.png"));
        System.out.println("test.png: " + testPng1);
        BufferedImage testPng2 = ImageUtil.getBufferedImageFromBase64String(ImageUtil.getBase64StringFromBufferedImage(testPng1));
        System.out.println("test.png(base64): " + testPng2);
        BufferedImage testJpeg = ImageUtil.read(new ClassPathResource("image/test.jpg"));
        System.out.println("test.jpg: " + testJpeg);
        BufferedImage testWebp = ImageUtil.read(new ClassPathResource("image/test.webp"));
        System.out.println("test.webp: " + testWebp);
        BufferedImage testAvif = ImageUtil.read(new ClassPathResource("image/test.avif"));
        System.out.println("test.avif: " + testAvif);
        BufferedImage fakeJpgPng = ImageUtil.read(new ClassPathResource("image/fake-jpg.png"));
        System.out.println("fake-jpg.png: " + fakeJpgPng);
        BufferedImage fakeAvifPng = ImageUtil.read(new ClassPathResource("image/fake-avif.png"));
        System.out.println("fake-avif.png: " + fakeAvifPng);
    }
}
