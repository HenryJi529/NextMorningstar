package com.morningstar.blog.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class BlogFakeUtilTest {

    @Autowired
    private BlogFakeUtil blogFakeUtil;

    @Test
    public void testFakeCategory() {
        System.out.println(blogFakeUtil.fakeCategory());
    }

    @Test
    public void testFakeTag() {
        System.out.println(blogFakeUtil.fakeTag());
    }


}
