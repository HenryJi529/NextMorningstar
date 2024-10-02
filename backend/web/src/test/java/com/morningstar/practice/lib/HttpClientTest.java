package com.morningstar.practice.lib;

import com.morningstar.util.JsonUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpClientTest {
    private final String rootUrl = "https://reqres.in/api";
    private CloseableHttpClient httpClient;

    private void setHeader(HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        httpRequestBase.setHeader("x-api-key", "reqres-free-v1");
        httpRequestBase.setHeader("User-Agent", "curl/8.7.1");
    }

    @BeforeEach
    public void init() {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void testGet() throws IOException {
        try {
            HttpGet get = new HttpGet(rootUrl + "/users?page=2");
            setHeader(get);
            CloseableHttpResponse response = httpClient.execute(get);

            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (SSLHandshakeException e) {
            System.out.println("一般是服务端错误");
        }
    }

    @Test
    public void testPost() throws IOException {
        try {
            HttpPost post = new HttpPost(rootUrl + "/users");
            setHeader(post);

            Map<String, String> params = new HashMap<>();
            params.put("name", "morpheus");
            params.put("job", "leader");

            StringEntity entity = new StringEntity(JsonUtil.objectMapper().writeValueAsString(params), ContentType.APPLICATION_JSON);
            post.setEntity(entity);

            String body = httpClient.execute(post, httpResponse -> {
                System.out.println(httpResponse.getStatusLine().getStatusCode());
                return EntityUtils.toString(httpResponse.getEntity());
            });
            System.out.println(body);
        } catch (SSLHandshakeException e) {
            System.out.println("一般是服务端错误");
        }
    }

    @AfterEach
    public void destroy() throws IOException {
        httpClient.close();
    }
}
