package com.example.gateway.filter;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 自定义响应装饰器。
 */
public class CustomServerHttpResponseDecorator extends ServerHttpResponseDecorator {
    private final ServerHttpRequest request;
    // 授权服务的服务名
    private final String authorizationServiceName;

    public CustomServerHttpResponseDecorator(ServerHttpResponse delegate, ServerHttpRequest request, String authorizationServiceName) {
        super(delegate);
        this.request = request;
        this.authorizationServiceName = authorizationServiceName;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        HttpStatusCode statusCode = super.getStatusCode();
        // 只处理响应状态码为 3xx 的响应，包括 302
        if (statusCode != null && statusCode.is3xxRedirection()) {
            // 这个 location 是由下游服务返回的重定向地址
            URI location = super.getHeaders().getLocation();
            if (location != null) {
                String query = location.getQuery();
                // 只处理重定向地址中不包含 query 参数或者 query 参数中包含 code=，即包含 code 参数的响应
                if (query == null || !query.contains("code=")) {
                    URI newLocation = getNewLocation(location);
                    super.getHeaders().setLocation(newLocation);
                }
            }
        }
        return super.writeWith(body);
    }

    // 获取新的重定向地址
    private URI getNewLocation(URI location) {
        URI newLocation;
        try {
            // 重原始的请求中获取 schema/host/port，即使用网关的 schema/host/port
            String newScheme = this.request.getURI().getScheme();
            String newHost = this.request.getURI().getHost();
            int newPort = this.request.getURI().getPort();
            // 新的路径需要拼接上授权服务的服务名，这样网关才能正确的转发请求
            String newPath = "/" + this.authorizationServiceName + location.getPath();
            // query 参数原样返回
            newLocation = new URI(newScheme, null, newHost, newPort, newPath, location.getQuery(), location.getFragment());
        } catch (URISyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
        return newLocation;
    }
}
