package com.morningstar.practice.spring;

import com.morningstar.practice.service.PracticeService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AopTest {
    private final PracticeService practiceService;

    @Test
    public void testNormalMethod() {
        String result = practiceService.doSomething("hello");
        System.out.println("==========");
        System.out.println(result);
    }

    @Test
    public void testExceptionMethod() {
        try {
            practiceService.throwException();
        } catch (Exception e) {
            System.out.println();
        }
    }
}
