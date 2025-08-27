package com.example.mcp.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    Logger log = LoggerFactory.getLogger(OrderService.class);

    /**
     * 例子来源：<a href="https://www.cnblogs.com/yjmyzz/p/18977523/spring-ai-learn-mcp-server-in-stdio-mode">spring-ai 学习系列(3)-MCP(stdio)</a>
     */
    @Tool(description = "根据订单号查询订单状态")
    public String queryOrderStatus(@ToolParam(description = "订单号，格式为 8 位数字，比如：25070601") String orderNo) {
        log.info("正在查询订单状态...{}", orderNo);
        return switch (orderNo) {
            case "25070601" -> "订单号：" + orderNo + "，订单状态：已发货";
            case "25070602" -> "订单号：" + orderNo + "，订单状态：已完成";
            case "25070603" -> "订单号：" + orderNo + "，订单状态：已取消";
            default -> "订单号：" + orderNo + "，订单状态：未知";
        };
    }
}
