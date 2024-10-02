package com.morningstar.kill.web.filter;

import com.morningstar.kill.constant.RedisConstant;
import com.morningstar.kill.pojo.bo.LoginUser;
import com.morningstar.kill.util.JwtUtil;
import com.morningstar.kill.util.WebUtil;
import com.morningstar.kill.pojo.vo.resp.R;
import com.morningstar.kill.pojo.vo.resp.ResponseCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取JWT
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.isBlank(token)){
            filterChain.doFilter(request, response);
            return;
        }

        // 解析JWT
        Claims claims = jwtUtil.parse(token);
        if(claims == null){
            WebUtil.renderJson(R.error(ResponseCode.TOKEN_ERROR), response);
            return;
        }
        String userId = claims.getSubject();

        // 从Redis中获取用户信息
        LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(RedisConstant.LOGIN_PREFIX + userId);
        if(loginUser == null){
            WebUtil.renderJson(R.error(ResponseCode.TOKEN_ERROR), response);
            return;
        }

        // 将认证token存入SecurityContextHolder
        SecurityContextHolder.getContext().
                setAuthentication(new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities()));

        // 放行
        filterChain.doFilter(request, response);
    }
}
