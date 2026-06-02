package dev.perfectbogus.kafkaconsumer.service.impl;

import dev.perfectbogus.api.event.UserEvent;
import dev.perfectbogus.kafkaconsumer.config.ConsumerProperties;
import dev.perfectbogus.kafkaconsumer.exception.KafkaConsumerException;
import dev.perfectbogus.kafkaconsumer.service.MessageConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumerServiceImpl implements MessageConsumerService {

    private final ConsumerProperties properties;

    @Override
    @KafkaListener(
            topics = "${kafka.topic.name}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumer(@Payload UserEvent event, Acknowledgment acknowledgment) {
        String correlationId = extractCorrelationId();
        log.info("[KAFKA] message received | topic={} eventId={} eventType={} userId={} correlationId={}",
                properties.getTopic().getName(),
                event.getEventId(),
                event.getEventType(),
                event.getUserId(),
                correlationId);

        try {
            processEvent(event);
            acknowledgment.acknowledge();
            log.info("[KAFKA] message processed successfully | eventId={}", event.getEventId());
        } catch (Exception ex) {
            log.error("[KAFKA] failed to process message | eventId={} error={}",
                    event.getEventId(), ex.getMessage(), ex);
            throw new KafkaConsumerException("Failed to process event: " + event.getEventId(), ex);
        }

    }

    @Override
    @KafkaListener(
            topics = "${kafka.topic.name}.DLT",
            groupId = "${kafka.consumer.group-id}-dlt",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeFromDlt(@Payload UserEvent event) {
        log.error("[KAFKA-DLT] Processing dead letter event | eventId={} eventType={} userId={}",
                event.getEventId(),
                event.getEventType(),
                event.getUserId());
    }

    private void processEvent(UserEvent event) {
        switch (event.getEventType()) {
            case USER_CREATED -> handleUserCreated(event);
            case USER_UPDATED -> handleUserUpdated(event);
            case USER_DELETED -> handleUserDeleted(event);
            default -> log.warn("[KAFKA] unhandled event type | eventType={}", event.getEventType());
        }
    }

    private void handleUserCreated(UserEvent event) {
        log.debug("[KAFKA] Handling USER_CREATED | userId={} email={}",
                event.getUserId(), event.getEmail());
        // your business logic here
    }

    private void handleUserUpdated(UserEvent event) {
        log.debug("[KAFKA] Handling USER_UPDATED | userId={}", event.getUserId());
        // your business logic here
    }

    private void handleUserDeleted(UserEvent event) {
        log.debug("[KAFKA] Handling USER_DELETED | userId={}", event.getUserId());
        // your business logic here
    }

    private String extractCorrelationId() {
        // Can be extended to extract from Kafka headers via ConsumerRecordMetadata
        return UUID.randomUUID().toString();
    }
}
