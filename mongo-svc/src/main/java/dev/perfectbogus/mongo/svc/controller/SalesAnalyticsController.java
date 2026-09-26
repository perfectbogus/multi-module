package dev.perfectbogus.mongo.svc.controller;

import dev.perfectbogus.mongo.svc.dto.order.*;
import dev.perfectbogus.mongo.svc.entity.Order;
import dev.perfectbogus.mongo.svc.service.SalesAnalyticsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/item-revenue")
    public List<ItemRevenueDto> getItemRevenue() {
        return analyticsSvc.getItemRevenueForDelivered();
    }

    @GetMapping("/orders-paypal")
    public List<Order> getOrdersPaidPaypal() {
        return analyticsSvc.getOrdersPaidByPaypal();
    }

    @GetMapping("/total-amount-gt")
    public List<Order> getOrdersTotalAmount() {
        return analyticsSvc.getOrdersTotalAmount();
    }

    @GetMapping("/order-by-city-gdl")
    public List<Order> getOrdersByCityGdl() {
        return analyticsSvc.getOrdersDeliveredByCity();
    }

    @GetMapping("/orders-by-client-name")
    public List<Order> getOrdersByClientName() {
        return analyticsSvc.getOrdersByClientName();
    }

    @GetMapping("/orders-by-audio-category")
    public List<Order> getOrdersByCategoryAudio() {
        return analyticsSvc.getOrdersByOneCategory();
    }

    @GetMapping("/orders-projected")
    public List<OrderProjectionDto> getOrdersProjected() {
        return analyticsSvc.getOrdersProjected();
    }

    @GetMapping("/orders-projected-renamed")
    public List<ProjectionRenameDto> getOrdersProjectedRenamed() {
        return analyticsSvc.getOrdersProjectedRenamed();
    }

    @GetMapping("/orders-projected-customer")
    public List<OrderProjectionCustomerDto> getOrdersProjectedCustomer() {
        return analyticsSvc.getOrdersProjectedCustomer();
    }

    @GetMapping("/orders-projected-operation")
    public List<OrderProjectedOperationDto> getOrdersProjectedOperation() {
        return analyticsSvc.getOrdersProjectedOperation();
    }

    @GetMapping("/order-projected-n-items")
    public List<OrderProjectNItems> getOrderProjectedNItems() {
        return analyticsSvc.getOrdersProjectedNItems();
    }

    @GetMapping("/count-documents-per-group")
    public List<CountDocsPerGroupDto> getCountDocsPerGroup() {
        return analyticsSvc.getCountDocsPerGroup();
    }

    @GetMapping("/summing-by-payment-method")
    public List<SummingPaymentMethodDto> getSummingByPaymentMethod() {
        return analyticsSvc.getSummingByPaymentMethod();
    }

    @GetMapping("/avg-per-city")
    public List<AvgCalculationDto> getAvgPerCity() {
        return analyticsSvc.getAvgPerCity();
    }

    @GetMapping("/group-stats")
    public List<GroupStatsDto> getGroupStats() {
        return analyticsSvc.getGroupStats();
    }

    @GetMapping("/get-overall-revenue")
    public OverallRevenueDto getOverallRevenue() {
        return analyticsSvc.getOverallRevenue();
    }

    @GetMapping("/item-details-per-order")
    public List<ItemUnwoundDto> getItemDetailsPerOrder() {
        return analyticsSvc.getItemDetailsPerOrder();
    }

    @GetMapping("/items-details/{category}")
    public List<ItemUnwoundDto> getItemUnwoundPerCategory(@PathVariable String category) {
        return analyticsSvc.getDetailsPerCategory(category);
    }
}
