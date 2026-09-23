package dev.perfectbogus.mongo.svc.controller;

import dev.perfectbogus.mongo.svc.dto.order.*;
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

    @GetMapping("/status-counts")
    public List<GroupCountDto> getStatusCounts() {
        return analyticsSvc.getStatusCounts();
    }

    @GetMapping("/delivered-stats")
    public RevenueStatsDto getDeliveredStats() {
        return analyticsSvc.getDeliveredStats();
    }

    @GetMapping("/city-revenue")
    public CityRevenueDto getCityRevenue(){
        return analyticsSvc.getCityRevenue();
    }

    @GetMapping("/unwound-items")
    public List<ItemProjectionDto> getUnwoundItems() {
        return analyticsSvc.getUnwoundItems();
    }

    @GetMapping("/category-units")
    public List<CategoryUnitsDto> getCategoryUnits() {
        return analyticsSvc.getUnitsSoldByCategory();
    }

}
