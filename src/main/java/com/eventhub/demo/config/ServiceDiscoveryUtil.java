package com.eventhub.demo.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Utility for service discovery related operations.
 * This class simplifies interactions with other microservices using discovery client.
 */
@Component
public class ServiceDiscoveryUtil {

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;
    private final Random random = new Random();

    public ServiceDiscoveryUtil(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
    }

    /**
     * Get all registered instances of service
     *
     * @param serviceId the service identifier
     * @return list of service instances
     */
    public List<ServiceInstance> getInstances(String serviceId) {
        return discoveryClient.getInstances(serviceId);
    }

    /**
     * Get random instance of a service for load balancing
     *
     * @param serviceId the service identifier
     * @return an optional containing a service instance if available
     */
    public Optional<ServiceInstance> getRandomInstances(String serviceId) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        if (instances.isEmpty()) return Optional.empty();

        ServiceInstance instance = instances.get(random.nextInt(instances.size()));
        return Optional.of(instance);
    }

    /**
     * Get the base URL for a service instance (http://host:port)
     *
     * @param instance the service instance
     * @return the base URL
     */
    public String getInstanceUrl(ServiceInstance instance) {
        return instance.getUri().toString();
    }

    /**
     * Make a GET request to another service by name and path
     *
     * @param serviceId    the service identifier (name)
     * @param path         the API path
     * @param responseType the expected response type
     * @return the response object or null if the service is not available
     */
    public <T> T callService(String serviceId, String path, Class<T> responseType) {
        return getRandomInstances(serviceId)
                .map(instance -> restTemplate.getForObject(
                        getInstanceUrl(instance) + path,
                        responseType
                ))
                .orElse(null);
    }

    /**
     * Get a list of all registered services
     *
     * @return list of service IDs
     */
    public List<String> getServices() {
        return discoveryClient.getServices();
    }
}