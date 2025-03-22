package com.example.gateway;

import com.example.gateway.loadbalancer.RandomLoadBalancerClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

@SpringBootApplication
@LoadBalancerClients(
        value = {
                // 使用 Eureka 作为注册中心时 name 必须与 Eureka 中注册的服务名保持一致，Eureka 在注册服务时会把名称转为大写形式，
                // 具体实现为 org.springframework.cloud.netflix.eureka.InstanceInfoFactory 类的 create 方
                @LoadBalancerClient(name = "EXAMPLE-USER", configuration = RandomLoadBalancerClientConfiguration.class)
        }
)
public class ExampleGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleGatewayApplication.class, args);
    }
}
