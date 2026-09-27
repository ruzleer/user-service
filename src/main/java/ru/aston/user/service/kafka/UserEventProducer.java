package ru.aston.user.service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final String topic;

    public UserEventProducer(KafkaTemplate<String, UserEvent> kafkaTemplate, @Value("${spring.kafka.topic.user-events:user-events}") String topic){
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(UserEvent event) {
        kafkaTemplate.send(topic, event.email(), event);
    }
}
