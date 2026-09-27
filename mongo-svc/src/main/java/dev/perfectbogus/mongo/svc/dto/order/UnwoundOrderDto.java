package dev.perfectbogus.mongo.svc.dto.order;

public record UnwoundOrderDto(String orderId, String status, Double totalAmount, ItemDto items) {

    public record ItemDto(
            String name,
            String category,
            String price,
            Integer qty
    ) {}
}
