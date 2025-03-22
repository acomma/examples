package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 自定义请求过滤器。参考 <a href="https://cloud.tencent.com/developer/article/2264294">Spring Cloud Security配置JWT和OAuth2的集成实现授权管理（三）</a>实现。
 */
@Component
public class CustomRequestFilter implements GlobalFilter, Ordered {
    private final ReactiveJwtDecoder reactiveJwtDecoder;

    public CustomRequestFilter(ReactiveJwtDecoder reactiveJwtDecoder) {
        this.reactiveJwtDecoder = reactiveJwtDecoder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String accessToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Authorization 请求头为空的不处理
        if (accessToken == null || accessToken.isBlank()) {
            return chain.filter(exchange);
        }
        // Authorization 请求头不是以 “Bearer ” 开头的不处理，比如 “Basic ”
        if (!accessToken.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        // 去掉 “Bearer ” 前缀
        accessToken = accessToken.replaceFirst("Bearer ", "");

        return reactiveJwtDecoder.decode(accessToken)
                .flatMap(jwt -> {
                    // 放入请求头的可以是从 JWT 中获取的用户信息，这里只是简单的把 Subject 信息放进去
                    ServerHttpRequest newRequest = exchange.getRequest().mutate().header("X-User", jwt.getSubject()).build();
                    ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
                    return chain.filter(newExchange);
                })
                .onErrorResume(throwable -> chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
