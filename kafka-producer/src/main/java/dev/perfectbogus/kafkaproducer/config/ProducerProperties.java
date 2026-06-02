package dev.perfectbogus.kafkaproducer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "kafka")
public class ProducerProperties {

    private Topic topic = new Topic();

    @Data
    public static class Topic {
        private String name;
        private String dltSuffix;
    }
}
