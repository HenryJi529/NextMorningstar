package com.morningstar.practice.spring;

import com.morningstar.practice.service.PracticeService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsyncTest {
    private final PracticeService practiceService;

    @Test
    public void testAsyncAnnotation() throws InterruptedException {
        practiceService.prolongedTask();
        Thread.sleep(4000);
    }
}
