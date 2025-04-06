package com.eventhub.demo.client;

import com.eventhub.demo.config.ServiceDiscoveryUtil;
import com.eventhub.demo.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@Slf4j
public class UserServiceClient {

    private static final String SERVICE_ID = "user-service";

    private final ServiceDiscoveryUtil serviceDiscoveryUtil;
    private final RestTemplate restTemplate;

    public UserServiceClient(ServiceDiscoveryUtil serviceDiscoveryUtil, RestTemplate restTemplate) {
        this.serviceDiscoveryUtil = serviceDiscoveryUtil;
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves userId by ID of the user to retrieve
     *
     * @param userId ID of the user to retrieve
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<UserDTO> getUserById(Long userId) {
        try {
            // Find an available instance of the user service
            Optional<ServiceInstance> serviceInstance = serviceDiscoveryUtil.getRandomInstances(SERVICE_ID);

            if (serviceInstance.isEmpty()) {
                log.warn("No instances of user-service available");
                return Optional.empty();
            }

            // Build the URL and make the request
            String url = serviceInstance.get().getUri() + "/api/users/" + userId;
            UserDTO userDTO = restTemplate.getForObject(url, UserDTO.class);

            return Optional.ofNullable(userDTO);
        } catch (RestClientException e) {
            log.error("Error calling user-service: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Checks if the user service is available
     *
     * @return true if at least one instance is registered and available
     */
    public boolean isServiceAvailable() {
        return !serviceDiscoveryUtil.getInstances(SERVICE_ID).isEmpty();
    }

    /**
     * Gets the username for given user ID
     * This method is failsafe and returns generic name if the service is unavailable
     * @param userId ID of the user
     * @return Username or fallback value
     */
    public String getUserName(Long userId) {
        return getUserById(userId)
                .map(user -> user.firstName() + " " + user.lastName())
                .orElse("User " + userId); // Fallback if service is unavailable
    }
}
