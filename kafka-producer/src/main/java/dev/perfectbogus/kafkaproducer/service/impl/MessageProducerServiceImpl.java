package dev.perfectbogus.kafkaproducer.service.impl;

import dev.perfectbogus.api.dto.MessageRequest;
import dev.perfectbogus.kafkaproducer.config.ProducerProperties;
import dev.perfectbogus.kafkaproducer.exception.KafkaPublishException;
import dev.perfectbogus.kafkaproducer.service.MessageProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerServiceImpl implements MessageProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProducerProperties properties;

    @Override
    public void send(MessageRequest request) {
        String topic = resolveTopic(request);
        ProducerRecord<String, Object> record = buildRecord(topic, request);

        kafkaTemplate.send(record)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[KAFKA] Failed to send | topic={} key={} error={}",
                                topic, request.getKey(), ex.getMessage(), ex);
                        throw new KafkaPublishException(
                                "Failed to publish to topic: " + topic, ex);
                    }
                    log.info("[KAFKA] Sent | topic={} partition={} offset={} key={}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            request.getKey());
                });
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> sendAsync(MessageRequest request) {
        String topic = resolveTopic(request);
        ProducerRecord<String, Object> record = buildRecord(topic, request);

        log.debug("[KAFKA] Sending async | topic={} key={}", topic, request.getKey());

        return kafkaTemplate.send(record).toCompletableFuture();
    }

    private ProducerRecord<String, Object> buildRecord(String topic, MessageRequest request) {
        ProducerRecord<String, Object> record =
                new ProducerRecord<>(topic, request.getKey(), request.getPayload());

        String correlationId = UUID.randomUUID().toString();
        record.headers().add("correlationId", correlationId.getBytes(StandardCharsets.UTF_8));
        record.headers().add("source", "kafka-producer".getBytes(StandardCharsets.UTF_8));

        return record;
    }

    private String resolveTopic(MessageRequest request) {
        return (request.getTopic() != null && !request.getTopic().isBlank())
                ? request.getTopic()
                : properties.getTopic().getName();
    }
}
