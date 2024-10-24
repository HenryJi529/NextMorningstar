package com.morningstar.kill.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningstar.kill.constant.RedisConstant;
import com.morningstar.kill.pojo.bo.LoginUser;
import com.morningstar.kill.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;


@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // websocket是支持跨域的，但可能需要兼容轮询的情况
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客户端订阅消息的请求前缀，topic一般用于广播推送，queue用于点对点推送
        registry.enableSimpleBroker("/topic", "/queue");
        // 服务端通知客户端的前缀，可以不设置，默认为user
        registry.setUserDestinationPrefix("/user");

        // 客户端发送消息的请求前缀
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);

        messageConverters.add(converter);
        return false;
    }

    /**
     * 配置客户端入站通道拦截器
     * 添加 ChannelInterceptor 拦截器，用于在消息发送前，从请求头中获取 token 并解析出用户信息(username)，用于点对点发送消息给指定用户
     *
     * @param registration 通道注册器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                // 如果是连接请求（CONNECT 命令），从请求头中取出 token 并设置到认证信息中
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 从连接头中提取授权令牌
                    String token = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

                    // 验证令牌格式并提取用户信息
                    if(StrUtil.isNotBlank(token)) {
                        Claims claims = jwtUtil.parse(token);
                        if(claims != null){
                            String userId = claims.getSubject();
                            LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(RedisConstant.LOGIN_PREFIX + userId);
                            if(loginUser != null){
                                accessor.setUser(()-> loginUser.getUser().getId().toString());
                                return message;
                            }
                        }
                    }
                    throw new RuntimeException("令牌无效");
                }
                // 不是连接请求，直接放行
                return ChannelInterceptor.super.preSend(message, channel);
            }
        });
    }
}
