package ru.aston.user.service.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UserEventProducerTest {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";


    private String topic;
    private Consumer<String, UserEvent> consumer;
    private UserEventProducer producer;

    @BeforeEach
    void setUp() {

        this.topic = "user-events-" + System.nanoTime();


        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        DefaultKafkaProducerFactory<String, UserEvent> producerFactory =
                new DefaultKafkaProducerFactory<>(producerProps);

        KafkaTemplate<String, UserEvent> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        this.producer = new UserEventProducer(kafkaTemplate, topic);

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "producer-test-group-" + System.nanoTime());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        JsonDeserializer<UserEvent> jsonDeserializer = new JsonDeserializer<>(UserEvent.class);
        jsonDeserializer.addTrustedPackages("ru.aston.user.service.kafka");

        DefaultKafkaConsumerFactory<String, UserEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        consumerProps,
                        new StringDeserializer(),
                        jsonDeserializer
                );

        this.consumer = consumerFactory.createConsumer();
        this.consumer.subscribe(List.of(topic));
        this.consumer.poll(Duration.ofMillis(300));
    }

    @Test
    void shouldSendCreateEvent() {
        UserEvent event = new UserEvent(UserOperation.CREATE, "test@email.com");

        producer.send(event);

        ConsumerRecord<String, UserEvent> record =
                KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofSeconds(5));

        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("test@email.com");
        assertThat(record.value()).isNotNull();
        assertThat(record.value().operation()).isEqualTo(UserOperation.CREATE);
        assertThat(record.value().email()).isEqualTo("test@email.com");
    }

    @Test
    void shouldSendDeleteEvent() {
        UserEvent event = new UserEvent(UserOperation.DELETE, "test@email.com");

        producer.send(event);

        ConsumerRecord<String, UserEvent> record =
                KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofSeconds(5));

        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo("test@email.com");
        assertThat(record.value()).isNotNull();
        assertThat(record.value().operation()).isEqualTo(UserOperation.DELETE);
        assertThat(record.value().email()).isEqualTo("test@email.com");
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }
}
