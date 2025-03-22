package com.example.gateway.loadbalancer;

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
import java.util.concurrent.ThreadLocalRandom;

/**
 * 参考 {@link org.springframework.cloud.loadbalancer.core.RandomLoadBalancer} 实现带灰度功能的随机负载均衡功能。大部分代码都来自 {@code RandomLoadBalancer} 类。
 */
public class CanaryRandomLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    private static final Log log = LogFactory.getLog(RandomLoadBalancer.class);

    private final String serviceId;

    private final SingletonSupplier<ServiceInstanceListSupplier> serviceInstanceListSingletonSupplier;

    public CanaryRandomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSingletonSupplier = SingletonSupplier.of(() -> serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new));
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

        int index = ThreadLocalRandom.current().nextInt(instances.size());

        ServiceInstance instance = instances.get(index);

        return new DefaultResponse(instance);
    }
}
