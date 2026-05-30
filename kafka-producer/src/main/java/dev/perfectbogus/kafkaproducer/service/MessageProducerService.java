package dev.perfectbogus.kafkaproducer.service;

import dev.perfectbogus.kafkaproducer.dto.MessageRequest;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface MessageProducerService {
    void send(MessageRequest request);
    CompletableFuture<SendResult<String, Object>> sendAsync(MessageRequest request);
}
