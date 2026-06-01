package dev.perfectbogus.api.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    @NotBlank
    private String eventId;

    @NotNull
    private EventType eventType;

    @NotNull
    private String userId;

    private String email;
    private String name;

    private Instant occurredOn;
    private String correlationId;

    public static UserEvent userCreated(String userId, String email, String name) {
        return UserEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(EventType.USER_CREATED)
                .userId(userId)
                .email(email)
                .name(name)
                .occurredOn(Instant.now())
                .build();
    }

    public static UserEvent userDeleted(String userId) {
        return UserEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(userId)
                .occurredOn(Instant.now())
                .build();
    }
}
