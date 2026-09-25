package ru.aston.user.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.user-events:user-events}")
    private final String topic;

    public void send(UserEvent event) {
        kafkaTemplate.send(topic, event.email(), event);
    }
}
