package com.example.mcp.server.dynamic.config;

import com.example.mcp.server.dynamic.router.DynamicRouterFunctionManager;
import com.example.mcp.server.dynamic.server.DynamicMcpServerManager;
import com.example.mcp.server.dynamic.utils.SpringBeanUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class DynamicMcpServerConfiguration implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtils.getInstance().setApplicationContext(applicationContext);
    }

    @Bean
    public DynamicMcpServerProperties dynamicMcpServerProperties() {
        return new DynamicMcpServerProperties();
    }

    @Bean
    public DynamicRouterFunctionManager dynamicRouterFunctionManager() {
        return new DynamicRouterFunctionManager();
    }

    @Bean
    @SuppressWarnings("unchecked")
    public RouterFunction<?> dynamicMcpRouterFunction(DynamicRouterFunctionManager dynamicRouterFunctionManager) {
        return request -> dynamicRouterFunctionManager.gettingRouterFunction()
                .route(request)
                .map(handler -> (HandlerFunction<ServerResponse>) handler);
    }

    @Bean
    public DynamicMcpServerManager dynamicMcpServerManager(@Qualifier("mcpServerObjectMapper") ObjectMapper objectMapper, DynamicMcpServerProperties dynamicMcpServerProperties, DynamicRouterFunctionManager dynamicRouterFunctionManager) {
        return new DynamicMcpServerManager(objectMapper, dynamicMcpServerProperties, dynamicRouterFunctionManager);
    }
}
