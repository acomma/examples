package com.example.mcp.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    /**
     * 根据城市名称查询城市的温度
     *
     * @param cityName 城市名称，比如：重庆
     */
    @GetMapping("/temperature")
    public Double getTemperature(String cityName) {
        System.out.println("正在查询天气温度..." + cityName);
        return switch (cityName) {
            case "漠河" -> -20.0;
            case "沈阳" -> -10.0;
            case "大连" -> -5.0;
            case "北京" -> 0.0;
            case "济南" -> 10.0;
            case "上海" -> 20.0;
            case "杭州" -> 30.0;
            case "广州" -> 35.0;
            case "三亚" -> 40.0;
            default -> 100.0;
        };
    }
}
