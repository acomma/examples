package com.example.mcp.server.dynamic.config;


import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ConfigurationProperties(DynamicMcpServerProperties.CONFIG_PREFIX)
public class DynamicMcpServerProperties {
    public static final String CONFIG_PREFIX = "spring.ai.mcp.server.dynamic";

    private final Map<String, ServerParameters> servers = new HashMap<>();

    public Map<String, ServerParameters> getServers() {
        return this.servers;
    }

    public static class ServerParameters {
        private boolean enabled = true;
        private String version = "1.0.0";
        private McpServerProperties.ServerProtocol protocol;
        private Set<String> toolObjectNames = new HashSet<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public McpServerProperties.ServerProtocol getProtocol() {
            return protocol;
        }

        public void setProtocol(McpServerProperties.ServerProtocol protocol) {
            this.protocol = protocol;
        }

        public Set<String> getToolObjectNames() {
            return toolObjectNames;
        }

        public void setToolObjectNames(Set<String> toolObjectNames) {
            this.toolObjectNames = toolObjectNames;
        }
    }
}
