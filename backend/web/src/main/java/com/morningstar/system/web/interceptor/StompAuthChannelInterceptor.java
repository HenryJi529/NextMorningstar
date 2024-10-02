package com.morningstar.system.web.interceptor;

import com.morningstar.infra.constant.RedisConstant;
import com.morningstar.system.pojo.bo.LoginUser;
import com.morningstar.system.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

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
                    LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(RedisConstant.AUTH_LOGIN + RedisConstant.KEY_SEPARATOR + userId);
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
}
