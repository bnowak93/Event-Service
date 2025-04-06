package com.eventhub.demo.controller;

import com.eventhub.demo.config.ServiceDiscoveryUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/discovery")
@RequiredArgsConstructor
@Tag(name = "Service Discovery", description = "API endpoints related to service discovery")
public class ServiceDiscoveryController {

    private final ServiceDiscoveryUtil serviceDiscoveryUtil;

    @GetMapping("/services")
    @Operation(summary = "List all services", description = "Returns a list of all registered services")
    @PreAuthorize("hasRole('ADMIN')")
    public List<String> getAllServices() {
        return serviceDiscoveryUtil.getServices();
    }


    @GetMapping("/services/{serviceId}")
    @Operation(summary = "Get service details", description = "Returns details about all instances of a specific service")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> getServicesDetails(@PathVariable String serviceId) {
        List<ServiceInstance> instances =
                serviceDiscoveryUtil.getInstances(serviceId);

        return instances.stream()
                .map(this::convertToInstanceMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/status")
    @Operation(summary = "Discovery Status", description = "Returns the overall status of service discovery")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getDiscoveryStatus() {
        List<String> services = serviceDiscoveryUtil.getServices();

        return Map.of(
                "totalServices",
                services.size(),
                "services",
                services,
                "instances",
                services.stream().mapToInt(service -> serviceDiscoveryUtil.getInstances(service).size()).sum(),
                "criticalServices",
                Map.of("user-service",
                       !serviceDiscoveryUtil.getInstances("user-service").isEmpty(),
                       "notification-service",
                       !serviceDiscoveryUtil.getInstances("notification-service").isEmpty()
                )

        );
    }

    private Map<String, Object> convertToInstanceMap(ServiceInstance instance) {
        return Map.of(
                "instanceId", instance.getInstanceId(),
                "serviceId", instance.getServiceId(),
                "host", instance.getHost(),
                "port", instance.getPort(),
                "uri", instance.getUri(),
                "secure", instance.isSecure(),
                "metadata", instance.getMetadata(),
                "scheme", instance.getScheme()
        );
    }
}
