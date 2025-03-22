package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 自定义响应过滤器。
 */
@Component
public class CustomResponseFilter implements GlobalFilter, Ordered {
    // 授权服务的名称，应该从配置文件中获取
    private String authorizationServiceName = "example-auth";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        // 只对请求路径中包含 /oauth2/authorize 和 /login 的响应进行处理
        if (path.contains("/oauth2/authorize") || path.contains("/login")) {
            CustomServerHttpResponseDecorator decorator = new CustomServerHttpResponseDecorator(exchange.getResponse(), exchange.getRequest(), authorizationServiceName);
            return chain.filter(exchange.mutate().response(decorator).build());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 在最终写出响应之前执行该过滤器
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }
}
