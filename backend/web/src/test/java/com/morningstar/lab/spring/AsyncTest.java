package com.morningstar.lab.spring;

import com.morningstar.lab.service.LabService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsyncTest {
    private final LabService labService;

    @Test
    public void testAsyncAnnotation() throws InterruptedException {
        labService.longTask();
        Thread.sleep(4000);
    }
}
