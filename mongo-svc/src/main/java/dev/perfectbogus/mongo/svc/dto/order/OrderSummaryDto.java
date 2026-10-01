package dev.perfectbogus.mongo.svc.dto.order;

public record OrderSummaryDto(String id, int itemCount, double totalAmount) {
}
