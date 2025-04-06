package com.eventhub.demo.monitoring;

import com.eventhub.demo.config.ServiceDiscoveryUtil;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DependentServicesHealthIndicator implements HealthIndicator {

    private final ServiceDiscoveryUtil serviceDiscoveryUtil;

    // List of services this application depends on
    private final String[] dependentServices = {
            "user-service",
            "notification-service"
    };

    public DependentServicesHealthIndicator(ServiceDiscoveryUtil serviceDiscoveryUtil) {
        this.serviceDiscoveryUtil = serviceDiscoveryUtil;
    }

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        boolean allServicesAvailable = true;

        // Check dependent services
        for (String serviceName : dependentServices) {
            int instanceCount = serviceDiscoveryUtil.getInstances(serviceName).size();
            details.put(serviceName + ".instances", instanceCount);

            if (instanceCount == 0) allServicesAvailable = false;
        }
        // Add summary information
        details.put("totalDependencies", dependentServices.length);

        // Build and return health status
        if (allServicesAvailable) {
            return Health.up()
                    .withDetails(details)
                    .build();
        } else {
            return Health.down()
                    .withDetails(details)
                    .build();
        }
    }
}
