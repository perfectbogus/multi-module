package dev.perfectbogus.mongo.svc.dto.order;

public record ItemUnwoundDto(
        String orderId,
        String itemName,
        String itemCategory,
        double itemPrice,
        int itemQty) {
}
