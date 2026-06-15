package com.morningstar.lab.spring;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RestClientTest {
    private final RestClient restClient;

    @Test
    public void testRestClient() {
        String response = restClient.get()
                .uri("https://jsonplaceholder.typicode.com/posts/1")
                .retrieve()
                .body(String.class);
        System.out.println(response);
    }
}
