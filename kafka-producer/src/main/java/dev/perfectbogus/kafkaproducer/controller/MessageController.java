package dev.perfectbogus.kafkaproducer.controller;

import dev.perfectbogus.api.dto.MessageRequest;
import dev.perfectbogus.kafkaproducer.service.MessageProducerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageProducerService producerService;

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> send(@Valid @RequestBody MessageRequest request) {
        log.debug("REST -> send | key={}", request.getKey());

        return producerService.sendAsync(request)
                .thenApply(result -> ResponseEntity.accepted()
                        .body("Delivered to partition %d at offset %d".formatted(
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset())));
    }
}
