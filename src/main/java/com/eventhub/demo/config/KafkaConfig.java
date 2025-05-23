package com.eventhub.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topics.event-created}")
    private String eventCreatedTopic;

    @Value("${app.kafka.topics.event-updated}")
    private String eventUpdatedTopic;

    @Value("${app.kafka.topics.event-deleted}")
    private String eventDeletedTopic;

    @Bean
    public NewTopic eventCreatedTopic() {
        return TopicBuilder.name(eventCreatedTopic)
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic eventUpdatedTopic() {
        return TopicBuilder.name(eventUpdatedTopic)
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic eventDeletedTopic() {
        return TopicBuilder.name(eventDeletedTopic)
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 2);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 200);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
