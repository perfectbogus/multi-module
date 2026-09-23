package dev.perfectbogus.mongo.svc.dto.order;

public record ItemProjectionDto(String orderId, String itemName, double itemPrice) {
}
