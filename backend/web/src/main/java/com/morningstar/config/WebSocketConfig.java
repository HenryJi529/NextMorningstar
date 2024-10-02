package com.morningstar.config;

import com.morningstar.common.pojo.bo.LoginUser;
import com.morningstar.constant.RedisConstant;
import com.morningstar.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Objects;


@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final RabbitProperties rabbitProperties;

    private final JwtUtil jwtUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws-connection")
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

        registry.setApplicationDestinationPrefixes("/ws");
    }

    /**
     * 配置入站通道拦截器，用于传递从WebSocket客户端接收到的消息
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            /**
             * 消息发送到通道前调用
             */
            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = Objects.requireNonNull(MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class));
                // 如果是连接请求，从请求头中取出token并设置到认证信息中
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 从连接头中提取授权令牌
                    String token = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
                    if (StringUtils.isNotBlank(token)) {
                        Claims claims = jwtUtil.parse(token);
                        if (claims != null) {
                            String userId = claims.getSubject();
                            LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(RedisConstant.LOGIN_PREFIX + userId);
                            if (loginUser != null) {
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                                accessor.setUser(authentication);
                                return ChannelInterceptor.super.preSend(message, channel);
                            } else {
                                throw new IllegalArgumentException("抱歉，您没有访问权限");
                            }
                        } else {
                            throw new IllegalArgumentException("抱歉，您没有访问权限");
                        }
                    } else {
                        throw new IllegalArgumentException("抱歉，您没有访问权限");
                    }
                }
                // 不是连接请求，直接放行
                return ChannelInterceptor.super.preSend(message, channel);
            }
        });
    }

    /**
     * 配置客出站通道拦截器，用于向WebSocket客户端发送服务器消息
     */
    @Override
    public void configureClientOutboundChannel(@NonNull ChannelRegistration registration) {
    }

}
