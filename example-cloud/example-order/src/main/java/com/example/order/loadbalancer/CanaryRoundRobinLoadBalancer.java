package com.example.order.loadbalancer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.*;
import org.springframework.util.function.SingletonSupplier;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 参考 {@link RoundRobinLoadBalancer} 实现带灰度功能的轮询负载均衡功能。大部分代码都来自 {@code RoundRobinLoadBalancer} 类。
 */
public class CanaryRoundRobinLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    private static final Log log = LogFactory.getLog(RoundRobinLoadBalancer.class);

    final AtomicInteger position;

    final String serviceId;

    private final SingletonSupplier<ServiceInstanceListSupplier> serviceInstanceListSingletonSupplier;

    public CanaryRoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        this(serviceInstanceListSupplierProvider, serviceId, new Random().nextInt(1000));
    }

    public CanaryRoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId, int seedPosition) {
        this.serviceId = serviceId;
        this.serviceInstanceListSingletonSupplier = SingletonSupplier.of(() -> serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new));
        this.position = new AtomicInteger(seedPosition);
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        List<String> candidates = ((RequestDataContext) request.getContext()).getClientRequest().getHeaders().get("X-Canary");
        String canary = candidates != null && !candidates.isEmpty() ? candidates.getFirst() : "false";

        ServiceInstanceListSupplier supplier = serviceInstanceListSingletonSupplier.obtain();
        return supplier.get(request)
                .next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances, canary));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier, List<ServiceInstance> serviceInstances, String canary) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances, canary);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, String canary) {
        List<ServiceInstance> normalInstances = new ArrayList<>();
        List<ServiceInstance> canaryInstances = new ArrayList<>();
        for (ServiceInstance instance : instances) {
            if (instance.getMetadata().get("canary") != null && instance.getMetadata().get("canary").equals("true")) {
                canaryInstances.add(instance);
            } else {
                normalInstances.add(instance);
            }
        }
        if (canary.equals("true")) {
            instances = canaryInstances.isEmpty() ? normalInstances : canaryInstances;
        } else {
            instances = normalInstances.isEmpty() ? canaryInstances : normalInstances;
        }

        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }

        // Do not move position when there is only 1 instance, especially some suppliers
        // have already filtered instances
        if (instances.size() == 1) {
            return new DefaultResponse(instances.get(0));
        }

        // Ignore the sign bit, this allows pos to loop sequentially from 0 to
        // Integer.MAX_VALUE
        int pos = this.position.incrementAndGet() & Integer.MAX_VALUE;

        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }
}
