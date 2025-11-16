package com.example.mcp.server.dynamic.server;

import com.example.mcp.server.dynamic.config.DynamicMcpServerProperties;
import com.example.mcp.server.dynamic.router.DynamicRouterFunctionManager;
import com.example.mcp.server.dynamic.utils.SpringBeanUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
import io.modelcontextprotocol.server.transport.WebFluxStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerProperties;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.reactive.function.server.RouterFunction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicMcpServerManager implements DisposableBean {
    private static final String BEAN_NAME_PREFIX = DynamicMcpServerManager.class.getName() + ".";
    private static final String TRANSPORT_PROVIDER_BEAN_NAME_PREFIX = BEAN_NAME_PREFIX + "transportProvider.";
    private static final String MCP_SERVER_BEAN_NAME_PREFIX = BEAN_NAME_PREFIX + "mcpServer.";

    private static final Map<String, DynamicMcpServerProperties.ServerParameters> ACTIVE_MCP_SERVERS = new ConcurrentHashMap<>();
    private static final Map<String, RouterFunction<?>> ACTIVE_ROUTER_FUNCTIONS = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;
    private final DynamicMcpServerProperties dynamicMcpServerProperties;
    private final DynamicRouterFunctionManager dynamicRouterFunctionManager;

    public DynamicMcpServerManager(ObjectMapper objectMapper, DynamicMcpServerProperties dynamicMcpServerProperties, DynamicRouterFunctionManager dynamicRouterFunctionManager) {
        this.objectMapper = objectMapper;
        this.dynamicMcpServerProperties = dynamicMcpServerProperties;
        this.dynamicRouterFunctionManager = dynamicRouterFunctionManager;
    }

    @Override
    public void destroy() throws Exception {
        stopMcpServer();
    }

    @EventListener(value = {ApplicationReadyEvent.class})
    public void onApplicationReady(ApplicationReadyEvent event) {
        startMcpServer();
    }

    private void startMcpServer() {
        for (Map.Entry<String, DynamicMcpServerProperties.ServerParameters> entry : this.dynamicMcpServerProperties.getServers().entrySet()) {
            String serverName = entry.getKey();
            DynamicMcpServerProperties.ServerParameters serverParameters = entry.getValue();
            if (!serverParameters.isEnabled()) {
                continue;
            }
            startMcpServer(serverName, serverParameters);
            ACTIVE_MCP_SERVERS.put(serverName, serverParameters);
        }
    }

    private void startMcpServer(String serverName, DynamicMcpServerProperties.ServerParameters serverParameters) {
        List<Object> toolObjects = new ArrayList<>();
        for (String toolObjectName : serverParameters.getToolObjectNames()) {
            toolObjects.add(SpringBeanUtils.getInstance().getBean(toolObjectName));
        }
        ToolCallback[] toolCallbacks = ToolCallbacks.from(toolObjects.toArray());

        if (serverParameters.getProtocol() == McpServerProperties.ServerProtocol.SSE) {
            String messageEndpoint = "/" + serverName + "/mcp/message";
            String sseEndpoint = "/" + serverName + "/sse";
            WebFluxSseServerTransportProvider transportProvider = WebFluxSseServerTransportProvider.builder()
                    .jsonMapper(new JacksonMcpJsonMapper(this.objectMapper))
                    .messageEndpoint(messageEndpoint)
                    .sseEndpoint(sseEndpoint)
                    .keepAliveInterval(Duration.ofHours(24))
                    .build();
            SpringBeanUtils.getInstance().registerSingleton(TRANSPORT_PROVIDER_BEAN_NAME_PREFIX + serverName, transportProvider);

            McpAsyncServer mcpServer = McpServer.async(transportProvider)
                    .serverInfo(serverName, serverParameters.getVersion())
                    .capabilities(McpSchema.ServerCapabilities.builder()
                            .tools(true)
                            .logging()
                            .build())
                    .tools(McpToolUtils.toAsyncToolSpecifications(toolCallbacks))
                    .build();
            SpringBeanUtils.getInstance().registerSingleton(MCP_SERVER_BEAN_NAME_PREFIX + serverName, mcpServer);

            RouterFunction<?> routerFunction = transportProvider.getRouterFunction();
            ACTIVE_ROUTER_FUNCTIONS.put(serverName, routerFunction);
            rebuildRouterFunction();
        }

        if (serverParameters.getProtocol() == McpServerProperties.ServerProtocol.STREAMABLE) {
            String messageEndpoint = "/" + serverName + "/mcp";
            WebFluxStreamableServerTransportProvider transportProvider = WebFluxStreamableServerTransportProvider.builder()
                    .jsonMapper(new JacksonMcpJsonMapper(this.objectMapper))
                    .messageEndpoint(messageEndpoint)
                    .keepAliveInterval(Duration.ofHours(24))
                    .disallowDelete(false)
                    .build();
            SpringBeanUtils.getInstance().registerSingleton(TRANSPORT_PROVIDER_BEAN_NAME_PREFIX + serverName, transportProvider);

            McpAsyncServer mcpServer = McpServer.async(transportProvider)
                    .serverInfo(serverName, serverParameters.getVersion())
                    .capabilities(McpSchema.ServerCapabilities.builder()
                            .tools(true)
                            .logging()
                            .build())
                    .tools(McpToolUtils.toAsyncToolSpecifications(toolCallbacks))
                    .build();
            SpringBeanUtils.getInstance().registerSingleton(MCP_SERVER_BEAN_NAME_PREFIX + serverName, mcpServer);

            RouterFunction<?> routerFunction = transportProvider.getRouterFunction();
            ACTIVE_ROUTER_FUNCTIONS.put(serverName, routerFunction);
            rebuildRouterFunction();
        }
    }

    private void rebuildRouterFunction() {
        RouterFunction<?> combined = ACTIVE_ROUTER_FUNCTIONS.values().stream().reduce(RouterFunction::andOther).orElse(null);
        dynamicRouterFunctionManager.refreshRouterFunction(combined);
    }

    private void stopMcpServer() {
        ACTIVE_MCP_SERVERS.forEach(this::stopMcpServer);
        ACTIVE_MCP_SERVERS.clear();
    }

    private void stopMcpServer(String serverName, DynamicMcpServerProperties.ServerParameters serverParameters) {
        ACTIVE_MCP_SERVERS.remove(serverName);
        rebuildRouterFunction();

        if (SpringBeanUtils.getInstance().containsSingleton(MCP_SERVER_BEAN_NAME_PREFIX + serverName)) {
            SpringBeanUtils.getInstance().destroySingleton(MCP_SERVER_BEAN_NAME_PREFIX + serverName);
        }

        if (SpringBeanUtils.getInstance().containsSingleton(TRANSPORT_PROVIDER_BEAN_NAME_PREFIX + serverName)) {
            if (ACTIVE_MCP_SERVERS.get(serverName).getProtocol() == McpServerProperties.ServerProtocol.SSE) {
                WebFluxSseServerTransportProvider transportProvider = SpringBeanUtils.getInstance().getBean(TRANSPORT_PROVIDER_BEAN_NAME_PREFIX + serverName, WebFluxSseServerTransportProvider.class);
                transportProvider.close();
            }
            if (ACTIVE_MCP_SERVERS.get(serverName).getProtocol() == McpServerProperties.ServerProtocol.STREAMABLE) {
                WebFluxStreamableServerTransportProvider transportProvider = SpringBeanUtils.getInstance().getBean(TRANSPORT_PROVIDER_BEAN_NAME_PREFIX + serverName, WebFluxStreamableServerTransportProvider.class);
                transportProvider.close();
            }
            SpringBeanUtils.getInstance().destroySingleton(TRANSPORT_PROVIDER_BEAN_NAME_PREFIX + serverName);
        }
    }
}
