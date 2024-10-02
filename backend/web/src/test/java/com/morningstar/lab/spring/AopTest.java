package com.morningstar.lab.spring;

import com.morningstar.lab.service.LabService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AopTest {
    private final LabService labService;

    @Test
    public void testNormalMethod() {
        String result = labService.doSomething("hello");
        System.out.println("==========");
        System.out.println(result);
    }

    @Test
    public void testExceptionMethod() {
        try {
            labService.throwException();
        } catch (Exception e) {
            System.out.println();
        }
    }
}
