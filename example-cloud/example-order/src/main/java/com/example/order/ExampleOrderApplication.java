package com.example.order;

import com.example.order.loadbalancer.CanaryRoundRobinLoadBalancerClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@LoadBalancerClients(
        value = {
                // 这里的名字得用小写形式
                @LoadBalancerClient(name = "example-user", configuration = CanaryRoundRobinLoadBalancerClientConfiguration.class),
                @LoadBalancerClient(name = "example-product", configuration = CanaryRoundRobinLoadBalancerClientConfiguration.class),
        }
)
public class ExampleOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleOrderApplication.class, args);
    }
}
