package com.morningstar.kill.config;

import cn.hutool.http.HttpStatus;
import com.morningstar.kill.web.filter.JwtAuthenticationFilter;
import com.morningstar.kill.util.WebUtil;
import com.morningstar.kill.pojo.vo.resp.ResponseCode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.List;

import com.morningstar.kill.pojo.vo.resp.R;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {
    /**
     * 定义密码加密、匹配器
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${security.cors.origins}")
    private String corsOrigins;

    /**
     * 白名单接口
     */
    public static final List<String> API_WHITE_LIST = List.of(
            "/api/docs/**",

            "/api/user/info/*",
            "/api/user/random/*",

            "/api/common/captcha",

            "/webjars/**",
            "/static/**",
            "/resources/**"
    );

    /**
     * 匿名访问接口
     */
    public static final List<String> API_ANONYMOUS_LIST = List.of(
            "/api/user/login",
            "/api/user/register"
    );

    /**
     * 配置AuthenticationManager
     * @param authenticationConfiguration AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception 异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return (request, response, accessDeniedException) -> WebUtil.renderJson(R.error(ResponseCode.NO_PERMISSION), response);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            int status = response.getStatus();

            R<Object> result = switch (status){
                case HttpStatus.HTTP_BAD_REQUEST -> R.error(ResponseCode.BAD_REQUEST);
                case HttpStatus.HTTP_NOT_FOUND -> R.error(ResponseCode.NOT_FOUND);
                case HttpStatus.HTTP_BAD_METHOD -> R.error(ResponseCode.BAD_METHOD);
                default -> R.error(ResponseCode.AUTHENTICATION_FAILED);
            };

            WebUtil.renderJson(result, response);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AccessDeniedHandler accessDeniedHandler, AuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        // 关闭CSRF保护
        http.csrf(AbstractHttpConfigurer::disable);

        // 设置Session: 不通过Session获取SecurityContext
        http.sessionManagement(sessionManagementCustomizer -> sessionManagementCustomizer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 权限配置
        http.authorizeHttpRequests(auth-> auth
                // 配置白名单
                .requestMatchers(API_WHITE_LIST.toArray(new String[0])).permitAll()
                // 配置匿名访问
                .requestMatchers(API_ANONYMOUS_LIST.toArray(new String[0])).anonymous()
                // 其他请求需要鉴权
                .anyRequest().authenticated());

        // 错误处理
        http.exceptionHandling((exception)-> exception.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler));

        // 配置Jwt过滤器的位置
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        //允许跨域
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of(corsOrigins));
            configuration.setAllowedMethods(List.of("*"));
            configuration.setAllowedHeaders(List.of("*"));
            return configuration;
        }));

        SecurityFilterChain chain = http.build();
        log.info(chain.toString());
        return chain;
    }
}