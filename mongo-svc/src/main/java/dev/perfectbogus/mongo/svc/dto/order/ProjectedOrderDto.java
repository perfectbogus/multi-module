package dev.perfectbogus.mongo.svc.dto.order;

public record ProjectedOrderDto(
        String orderId,
        String status,
        Double totalAmount
) {
}
