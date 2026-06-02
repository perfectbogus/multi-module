package dev.perfectbogus.kafkaconsumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kafka")
public class ConsumerProperties {
    private Topic topic = new Topic();
    private Consumer consumer = new Consumer();

    @Data
    public static class Topic {
        private String name;
        private String dltSuffix;
    }

    @Data
    public static class Consumer {
        private String bootstrapServers;
        private String groupId;
        private String autoOffsetReset;
        private Boolean enableAutoCommit;
        private Integer maxPollRecords;
        private Integer concurrency;
        private Backoff backoff = new Backoff();
    }

    @Data
    public static class Backoff {
        private Integer maxRetries;
        private Long initialInterval;
        private Double multiplier;
        private Long maxInterval;
    }
}
