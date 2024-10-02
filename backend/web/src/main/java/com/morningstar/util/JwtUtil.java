package com.morningstar.util;

import com.morningstar.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtUtil {
    // Token前缀
    public final String TOKEN_PREFIX = "Bearer ";
    // 签名算法
    public final SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;
    // 声明字段名
    public final String USERNAME_CLAIM = "username";
    public final String ROLE_CLAIM = "role";
    private final JwtProperties jwtProperties;

    private SecretKey getSecretKey() {
        return new SecretKeySpec(Base64.getDecoder().decode(jwtProperties.getSecretKey()), algorithm.getJcaName());
    }

    /**
     * 生成Token
     */
    public String create(UUID id, String username, String role, Long ttl) {
        if (ttl == null) {
            ttl = jwtProperties.getTtl();
        }

        return TOKEN_PREFIX + Jwts
                .builder()
                .setSubject(id.toString())
                .claim(ROLE_CLAIM, role)
                .claim(USERNAME_CLAIM, username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ttl))
                .signWith(getSecretKey(), algorithm)
                .compact();
    }

    public String create(UUID id, String username, String role) {
        return create(id, username, role, null);
    }

    /**
     * 获取Token解析器
     */
    private JwtParser getParser() {
        return Jwts.parserBuilder().setSigningKey(getSecretKey()).build();
    }

    /**
     * 解析Token，如果非法返回null(包含超时检测)
     */
    public Claims parse(String token) {
        if (!token.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        try {
            String content = token.substring(TOKEN_PREFIX.length());
            return getParser().parseClaimsJws(content).getBody();
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsername(String token) {
        return Objects.requireNonNull(parse(token)).get(USERNAME_CLAIM).toString();
    }

    /**
     * 从Token中获取用户角色
     */
    public String getRole(String token) {
        return Objects.requireNonNull(parse(token)).get(ROLE_CLAIM).toString();
    }
}
