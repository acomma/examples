package com.example.mcp.server.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    @Tool(description = "根据城市名称查询城市的温度")
    public Double getTemperature(@ToolParam(description = "城市名称，比如：重庆") String cityName) {
        System.out.println("正在查询天气温度..." + cityName);
        return switch (cityName) {
            case "北京" -> -10.0;
            case "重庆" -> 20.0;
            case "三亚" -> 30.0;
            default -> null;
        };
    }
}
