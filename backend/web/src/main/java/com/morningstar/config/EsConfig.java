package com.morningstar.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.morningstar.properties.EsProperties;
import com.morningstar.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EsConfig {
    private final EsProperties esProperties;

    @Bean
    public RestClientTransport restClientTransport() {
        RestClient restClient = RestClient.builder(new HttpHost(esProperties.getHostname(), esProperties.getPort())).build();
        return new RestClientTransport(restClient, new JacksonJsonpMapper(JsonUtil.objectMapper()));
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClientTransport transport) {
        return new ElasticsearchClient(transport);
    }
}
