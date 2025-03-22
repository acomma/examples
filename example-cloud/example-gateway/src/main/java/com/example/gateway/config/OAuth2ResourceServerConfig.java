package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 参考 <a href="https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/jwt.html">OAuth 2.0 Resource Server JWT</a>。
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2ResourceServerConfig {
    @Bean
    @Order(1)
    public SecurityWebFilterChain resourceServerSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchanges -> exchanges
                .pathMatchers("/example-user/**").hasAuthority("SCOPE_user")
                .pathMatchers("/example-product/**").hasAuthority("SCOPE_product")
                .pathMatchers("/example-order/**").hasAuthority("SCOPE_order")
                .pathMatchers("/example-auth/**").permitAll()
                // 在默认的授权页面点击 Submit Consent 按钮后请求的地址
                .pathMatchers("/oauth2/authorize").permitAll()
                // 在默认的登录页面点击 Sign in 按钮后请求的地址
                .pathMatchers("/login").permitAll()
                .anyExchange().authenticated());
        http.oauth2ResourceServer(configurer -> configurer.jwt(Customizer.withDefaults()));
        // 因为默认的登录页面带有隐藏的 csrf 字段，点击 Sign in 按钮后会一起发送到网关，
        // 而网关并不认识它，因为它是授权服务返回的
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }
}
