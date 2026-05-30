package dev.perfectbogus.kafkaproducer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @NotBlank(message = "key must not be blank")
    private String key;

    @NotNull(message = "payload must not be null")
    private Object payload;

    private String topic;
}
