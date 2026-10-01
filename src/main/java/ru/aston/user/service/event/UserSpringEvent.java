package ru.aston.user.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.aston.user.service.kafka.UserEvent;

@Getter
public class UserSpringEvent extends ApplicationEvent {
    private final UserEvent userEvent;

    public UserSpringEvent(Object source, UserEvent userEvent) {
        super(source);
        this.userEvent = userEvent;
    }
}
