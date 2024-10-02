package com.morningstar.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class HttpConfig {
    @Bean
    public CloseableHttpClient httpClient() {
        // 配置连接池
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(200) // 整个连接池的最大连接数
                .setMaxConnPerRoute(50) // 每个路由(目标主机)的最大连接数
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(Timeout.ofSeconds(30)) // 套接字读取超时时间(无数据传输的最长等待时间)
                        .build())
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setConnectTimeout(Timeout.ofSeconds(5))   // 建立连接的超时时间
                        .setSocketTimeout(Timeout.ofSeconds(60))   // 请求/响应传输超时
                        .build())
                .build();


        return HttpClients.custom()
                .setConnectionManager(connectionManager) // 使用配置好的连接池管理器
                .setConnectionManagerShared(true)  // 共享连接管理器(多个HttpClient实例共享)
                .evictExpiredConnections() // 启用过期连接清理
                .evictIdleConnections(Timeout.ofSeconds(30))  // 清理空闲超过30秒的连接
                .setRetryStrategy(new DefaultHttpRequestRetryStrategy(3, TimeValue.ofSeconds(1)))  // 重试策略
                .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public RestTemplate restTemplate() {
        // 默认使用SimpleClientHttpRequestFactory连接池
        return new RestTemplate(httpRequestFactory());
    }
}
