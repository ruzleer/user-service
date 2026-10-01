package ru.aston.user.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.aston.user.service.kafka.UserEventProducer;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserEventProducer userEventProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserEvent(UserSpringEvent springEvent){
        log.info("Transaction succesfully commited. Sending message to Kafka/");

        userEventProducer.send(springEvent.getUserEvent());
    }
}
