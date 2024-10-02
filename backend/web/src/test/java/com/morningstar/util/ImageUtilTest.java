package com.morningstar.util;

import org.junit.jupiter.api.Test;

public class ImageUtilTest {
    @Test
    public void testIsValidImageFilename(){
        assert ImageUtil.isValidImageFilename("test.png");
        assert !ImageUtil.isValidImageFilename("test.txt");
        assert !ImageUtil.isValidImageFilename("*test.jpeg");
        assert !ImageUtil.isValidImageFilename("a/test.jpeg");
    }
}
