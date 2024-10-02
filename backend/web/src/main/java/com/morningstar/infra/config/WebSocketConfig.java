package com.morningstar.infra.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final RabbitProperties rabbitProperties;
    private final ChannelInterceptor stompAuthChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*") // websocket是支持跨域的，但可能需要兼容轮询的情况
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/topic/", "/queue/", "/exchange/")
                .setRelayHost(rabbitProperties.getHost())
                .setRelayPort(61613)
                .setVirtualHost(rabbitProperties.getVirtualHost())
                .setClientLogin(rabbitProperties.getUsername())
                .setClientPasscode(rabbitProperties.getPassword())
                .setSystemLogin(rabbitProperties.getUsername())
                .setSystemPasscode(rabbitProperties.getPassword());

        registry.setApplicationDestinationPrefixes("/chat");
    }

    /**
     * 配置入站通道拦截器，用于传递从WebSocket客户端接收到的消息
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }

    /**
     * 配置客出站通道拦截器，用于向WebSocket客户端发送服务器消息
     */
    @Override
    public void configureClientOutboundChannel(@NonNull ChannelRegistration registration) {
    }

}
