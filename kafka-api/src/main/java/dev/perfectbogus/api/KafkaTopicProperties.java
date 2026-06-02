package dev.perfectbogus.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaTopicProperties {
    private String name;
    private String dltSuffix;
}
