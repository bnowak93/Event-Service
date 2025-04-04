package com.eventhub.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.servlet.context-path:/event-service}")
    private String contextPath;

    @Bean
    public OpenAPI openAPI() {
        // Direct server configuration
        Server directServer = new Server();
        directServer.setUrl("http://localhost:8080" + contextPath);
        directServer.setDescription("Direct DEV Server");

        // Gateway server configuration - add if you know the gateway URL
        Server gatewayServer = new Server();
        gatewayServer.setUrl("http://localhost:8081/api");
        gatewayServer.setDescription("API Gateway Server");

        Contact contact = new Contact();
        contact.setEmail("info@eventhub.com");
        contact.setName("EventHub API Support");
        contact.setUrl("https://www.evenhub.com");

        License license = new License()
                .name("Apache License, Version 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("EventHub API")
                .version("1.0.0")
                .contact(contact)
                .description("This API provides endpoints to manage events")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(directServer,gatewayServer));
    }
}
