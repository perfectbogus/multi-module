package dev.perfectbogus.kafkaconsumer.service;

import dev.perfectbogus.api.event.UserEvent;
import org.springframework.kafka.support.Acknowledgment;

public interface MessageConsumerService {
    void consumer(UserEvent event, Acknowledgment acknowledgment);
    void consumeFromDlt(UserEvent event);
}
