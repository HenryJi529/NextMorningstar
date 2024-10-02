package com.morningstar.practice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestHttpClient {
    private CloseableHttpClient httpClient;

    private final String rootUrl = "https://reqres.in/api";

    @BeforeEach
    public void init() {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void testGet() throws IOException {
        HttpGet get = new HttpGet( rootUrl + "/api/users?page=2");

        CloseableHttpResponse response = httpClient.execute(get);

        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testPost() throws IOException {
        HttpPost post = new HttpPost(rootUrl + "/api/users");

        Map<String, String> params = new HashMap<>();
        params.put("name", "morpheus");
        params.put("job", "leader");

        StringEntity entity = new StringEntity(new ObjectMapper().writeValueAsString(params), ContentType.APPLICATION_JSON);
        post.setHeader("x-api-key","reqres-free-v1");
        post.setEntity(entity);

        String body = httpClient.execute(post, httpResponse -> {
            System.out.println(httpResponse.getStatusLine().getStatusCode());
            return EntityUtils.toString(httpResponse.getEntity());
        });
        System.out.println(body);
    }

    @AfterEach
    public void destroy() throws IOException {
        httpClient.close();
    }
}
