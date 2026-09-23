package dev.perfectbogus.mongo.svc.controller;

import dev.perfectbogus.mongo.svc.dto.order.ProjectedOrderDto;
import dev.perfectbogus.mongo.svc.dto.order.SimpleCountDto;
import dev.perfectbogus.mongo.svc.entity.Order;
import dev.perfectbogus.mongo.svc.service.SalesAnalyticsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@AllArgsConstructor
public class SalesAnalyticsController {
    private final SalesAnalyticsService analyticsSvc;

    @GetMapping("/delivered")
    public List<Order> getDelivered() {
        return analyticsSvc.getDeliveredOrders();
    }

    @GetMapping("/projected")
    public List<ProjectedOrderDto> getProjected() {
        return analyticsSvc.getProjectedOrders();
    }

    @GetMapping("/top2")
    public List<Order> getTop2(){
        return analyticsSvc.getTop2Orders();
    }

    @GetMapping("/credit-card-count")
    public SimpleCountDto getCreditCardCount() {
        return analyticsSvc.getCreditCardOrdersCount();
    }

}
