package dev.perfectbogus.mongo.svc.dto.order;

public record OrderProjectionDto (String orderId, String status, double totalAmount){
}
