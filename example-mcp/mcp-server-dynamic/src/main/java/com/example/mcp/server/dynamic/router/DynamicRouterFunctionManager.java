package com.example.mcp.server.dynamic.router;

import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

public class DynamicRouterFunctionManager {
    private static final RouterFunction<?> EMPTY_ROUTER_FUNCTION = request -> Mono.empty();

    private RouterFunction<?> routerFunction;

    public DynamicRouterFunctionManager() {
        this.routerFunction = EMPTY_ROUTER_FUNCTION;
    }

    public synchronized void refreshRouterFunction(RouterFunction<?> routerFunction) {
        this.routerFunction = routerFunction != null ? routerFunction : EMPTY_ROUTER_FUNCTION;
    }

    public synchronized RouterFunction<?> gettingRouterFunction() {
        return this.routerFunction;
    }
}
