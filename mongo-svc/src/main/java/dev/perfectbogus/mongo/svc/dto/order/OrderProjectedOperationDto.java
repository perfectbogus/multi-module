package dev.perfectbogus.mongo.svc.dto.order;

public record OrderProjectedOperationDto(String orderId, double discountedTotal) {
}
