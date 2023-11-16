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

public class CustomServerHttpResponseDecorator extends ServerHttpResponseDecorator {
    private final ServerHttpRequest request;
    private final String authorizationServiceName;

    public CustomServerHttpResponseDecorator(ServerHttpResponse delegate, ServerHttpRequest request, String authorizationServiceName) {
        super(delegate);
        this.request = request;
        this.authorizationServiceName = authorizationServiceName;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        HttpStatusCode statusCode = super.getStatusCode();
        if (statusCode != null && statusCode.is3xxRedirection()) {
            URI location = super.getHeaders().getLocation();
            if (location != null) {
                String query = location.getQuery();
                if (query == null || !query.contains("code=")) {
                    URI newLocation = getNewLocation(location);
                    super.getHeaders().setLocation(newLocation);
                }
            }
        }
        return super.writeWith(body);
    }

    private URI getNewLocation(URI location) {
        URI newLocation;
        try {
            String newScheme = request.getURI().getScheme();
            String newHost = request.getURI().getHost();
            int newPort = request.getURI().getPort();
            String newPath = "/" + authorizationServiceName + location.getPath();
            newLocation = new URI(newScheme, null, newHost, newPort, newPath, location.getQuery(), location.getFragment());
        } catch (URISyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
        return newLocation;
    }
}
