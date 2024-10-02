package com.morningstar.config;

import com.morningstar.common.web.filter.JwtAuthenticationFilter;
import com.morningstar.util.WebUtil;
import com.morningstar.constant.ResponseCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import com.morningstar.constant.R;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfig {
    /**
     * 定义密码加密、匹配器
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${security.cors.origins}")
    private String corsOrigins;

    /**
     * 白名单接口(任意方法)
     */
    private static final List<String> API_WHITE_LIST_ALL_METHOD = List.of(
            "/api/docs/**",

            "/api/user/random/*",

            "/api/common/captcha",

            "/api/love/**",

            "/webjars/**",
            "/static/**",
            "/resources/**",

            "/api/ws-connection/**"
    );

    /**
     * 白名单接口(GET方法)
     */
    private static final List<String> API_WHITE_LIST_GET_METHOD = List.of(
            "/api/blog/article/**",
            "/api/blog/category",
            "/api/blog/tag",
            "/api/blog/comment/**"
    );

    /**
     * 匿名访问接口
     */
     private static final List<String> API_ANONYMOUS_LIST = List.of(
            "/api/user/auth/login",
            "/api/user/auth/register"
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
            R<Object> result = R.error(ResponseCode.AUTHENTICATION_FAILED);
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
                .requestMatchers(API_WHITE_LIST_ALL_METHOD.toArray(new String[0])).permitAll()
                .requestMatchers(HttpMethod.GET, API_WHITE_LIST_GET_METHOD.toArray(new String[0])).permitAll()
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