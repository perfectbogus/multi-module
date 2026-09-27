package dev.perfectbogus.mongo.svc.dto.order;

public record ItemSubTotalDto(
        String itemName,
        double price,
        int quantity,
        double itemSubTotal) {
}
