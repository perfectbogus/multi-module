package dev.perfectbogus.mongo.svc.service;

import dev.perfectbogus.mongo.svc.dto.order.*;
import dev.perfectbogus.mongo.svc.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class SalesAnalyticsService {
    private final MongoTemplate mongoTemplate;
    private static final String COLLECTION = "orders";

    public List<Order> getDeliveredOrders() {
        MatchOperation match = match(Criteria.where("status").is("DELIVERED"));
        Aggregation agg = newAggregation(match);
        return mongoTemplate.aggregate(agg, COLLECTION, Order.class).getMappedResults();
    }

    public List<ProjectedOrderDto> getProjectedOrders() {
        ProjectionOperation project = project("orderId", "status", "totalAmount").andExclude("_id");
        Aggregation agg = newAggregation(project);
        return mongoTemplate.aggregate(agg, COLLECTION, ProjectedOrderDto.class).getMappedResults();
    }

    public List<Order> getTop2Orders() {
        SortOperation sort = sort(Sort.Direction.DESC, "totalAmount");
        LimitOperation limit = limit(2);
        Aggregation agg = newAggregation(sort, limit);
        return mongoTemplate.aggregate(agg, COLLECTION, Order.class).getMappedResults();
    }

    public SimpleCountDto getCreditCardOrdersCount() {
        MatchOperation match = match(Criteria.where("paymentMethod").is("CREDIT_CARD"));
        CountOperation count = count().as("creditCardOrdersCount");
        Aggregation agg = newAggregation(match, count);
        return mongoTemplate.aggregate(agg, COLLECTION, SimpleCountDto.class).getUniqueMappedResult();
    }


    public List<GroupCountDto> getStatusCounts() {
        GroupOperation group = group("status").count().as("totalOrders");
        Aggregation agg  = newAggregation(group);
        return mongoTemplate.aggregate(agg, COLLECTION, GroupCountDto.class).getMappedResults();
    }

    public RevenueStatsDto getDeliveredStats() {
        MatchOperation match = match(Criteria.where("status").is("DELIVERED"));
        GroupOperation group = group()
                .sum("totalAmount").as("totalRevenue")
                .avg("totalAmount").as("averageOrderValue");
        Aggregation agg = newAggregation(match, group);
        return mongoTemplate.aggregate(agg, COLLECTION, RevenueStatsDto.class).getUniqueMappedResult();
    }

    public CityRevenueDto getCityRevenue() {
        GroupOperation group = group("customer.city")
                .sum("totalAmount").as("cityRevenue");
        Aggregation agg = newAggregation(group);
        return mongoTemplate.aggregate(agg, COLLECTION, CityRevenueDto.class).getUniqueMappedResult();
    }

    public List<ItemProjectionDto> getUnwoundItems() {
        UnwindOperation unwind = unwind("items");
        ProjectionOperation projection = project()
                .and("orderId").as("orderId")
                .and("items.name").as("itemName")
                .and("items.price").as("itemPrice")
                .andExclude("_id");
        Aggregation agg = newAggregation(unwind, projection);
        return mongoTemplate.aggregate(agg, COLLECTION, ItemProjectionDto.class).getMappedResults();
    }
}
