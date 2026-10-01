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

    public List<CategoryUnitsDto> getUnitsSoldByCategory() {
        UnwindOperation unwind = unwind("items");
        GroupOperation group = group("items.category")
                .sum("items.qty").as("totalUnitsSold");
        Aggregation agg = newAggregation(unwind, group);
        return mongoTemplate.aggregate(agg, COLLECTION, CategoryUnitsDto.class).getMappedResults();
    }

    public List<ItemRevenueDto> getItemRevenueForDelivered() {
        MatchOperation match = match(Criteria.where("status").is("DELIVERED"));
        UnwindOperation unwind = unwind("items");
        GroupOperation group = group("items.name")
                .sum(ArithmeticOperators.valueOf("items.price")
                        .multiplyBy("items.qty"))
                .as("itemRevenue");

        SortOperation sort = sort(Sort.Direction.DESC, "itemRevenue");

        Aggregation agg = newAggregation(match, unwind, group, sort);
        return mongoTemplate.aggregate(agg, COLLECTION, ItemRevenueDto.class).getMappedResults();
    }

    public List<Order> getOrdersPaidByPaypal() {
        MatchOperation match = match(Criteria.where("paymentMethod").is("PAYPAL"));
        Aggregation agg = newAggregation(match);
        return mongoTemplate.aggregate(agg, COLLECTION, Order.class).getMappedResults();
    }

    public List<Order> getOrdersTotalAmount() {
        MatchOperation match = match(Criteria.where("totalAmount").gt(100));
        Aggregation agg = newAggregation(match);
        return mongoTemplate.aggregate(agg, COLLECTION, Order.class).getMappedResults();
    }

    public List<Order> getOrdersDeliveredByCity() {
        MatchOperation match = match(
                Criteria.where("status").is("DELIVERED")
                        .and("customer.city").is("Guadalajara"));
        Aggregation agg = newAggregation(match);
        return mongoTemplate.aggregate(agg, COLLECTION, Order.class).getMappedResults();
    }

    public List<Order> getOrdersByClientName() {
        MatchOperation match = match(Criteria.where("customer.name").is("Ana"));
        Aggregation agg = newAggregation(match);
        return mongoTemplate.aggregate(agg, COLLECTION, Order.class).getMappedResults();
    }

    public List<Order> getOrdersByOneCategory() {
        MatchOperation match = match(Criteria.where("items.category").is("Audio"));
        Aggregation agg = newAggregation(match);
        return mongoTemplate.aggregate(agg, COLLECTION, Order.class).getMappedResults();
    }

    public List<OrderProjectionDto> getOrdersProjected() {
        ProjectionOperation projection = project("orderId", "status","totalAmount").andExclude("_id");
        Aggregation agg = newAggregation(projection);
        return mongoTemplate.aggregate(agg, COLLECTION, OrderProjectionDto.class).getMappedResults();
    }

    public List<ProjectionRenameDto> getOrdersProjectedRenamed() {
        ProjectionOperation project = project("orderId").and("totalAmount").as("orderTotal").andExclude("_id");
        Aggregation agg = newAggregation(project);
        return mongoTemplate.aggregate(agg, COLLECTION, ProjectionRenameDto.class).getMappedResults();
    }

    public List<OrderProjectionCustomerDto> getOrdersProjectedCustomer() {
        ProjectionOperation project = project("orderId").and("customer.name").as("customerName")
                .and("customer.city").as("customerCity").andExclude("_id");
        Aggregation agg = newAggregation(project);
        return mongoTemplate.aggregate(agg, COLLECTION, OrderProjectionCustomerDto.class).getMappedResults();
    }

    public List<OrderProjectedOperationDto> getOrdersProjectedOperation() {
        ProjectionOperation project = project("orderId")
                .and("totalAmount").multiply(0.90).as("discountedTotal")
                .andExclude("_id");
        Aggregation agg = newAggregation(project);
        return mongoTemplate.aggregate(agg, COLLECTION, OrderProjectedOperationDto.class).getMappedResults();
    }

    public List<OrderProjectNItems> getOrdersProjectedNItems() {
        ProjectionOperation project = project("orderId")
                .and(ArrayOperators.Size.lengthOfArray("items")).as("totalItems");
        Aggregation agg = newAggregation(project);
        return mongoTemplate.aggregate(agg, COLLECTION, OrderProjectNItems.class).getMappedResults();
    }

    public List<CountDocsPerGroupDto> getCountDocsPerGroup() {
        GroupOperation group = group("status").count().as("totalOrders");
        Aggregation agg = newAggregation(group);
        return mongoTemplate.aggregate(agg, COLLECTION, CountDocsPerGroupDto.class).getMappedResults();
    }

    public List<SummingPaymentMethodDto> getSummingByPaymentMethod() {
        GroupOperation group = group("paymentMethod")
                .sum("totalAmount").as("grandTotal");
        Aggregation agg = newAggregation(group);
        return mongoTemplate.aggregate(agg, COLLECTION, SummingPaymentMethodDto.class).getMappedResults();
    }

    public List<AvgCalculationDto> getAvgPerCity() {
        GroupOperation group = group("customer.city")
                .avg("totalAmount").as("avgOrderValue");
        Aggregation agg = newAggregation(group);
        return mongoTemplate.aggregate(agg, COLLECTION, AvgCalculationDto.class).getMappedResults();
    }

    public List<GroupStatsDto> getGroupStats() {
        GroupOperation group = group("status")
                .min("totalAmount").as("minTotal")
                .max("totalAmount").as("maxTotal");
        Aggregation agg = newAggregation(group);
        return mongoTemplate.aggregate(agg, COLLECTION, GroupStatsDto.class).getMappedResults();
    }

    public OverallRevenueDto getOverallRevenue() {
        GroupOperation group = group()
                .sum("totalAmount").as("overallRevenue");
        Aggregation agg = newAggregation(group);
        return mongoTemplate.aggregate(agg, COLLECTION, OverallRevenueDto.class).getUniqueMappedResult();
    }

    public List<ItemUnwoundDto> getItemDetailsPerOrder() {
        UnwindOperation unwind = unwind("items");
        ProjectionOperation project = project("orderId")
                .and("items.name").as("itemName")
                .and("items.category").as("itemCategory")
                .and("items.price").as("itemPrice")
                .and("items.qty").as("itemQty");
        Aggregation agg = newAggregation(unwind, project);
        return mongoTemplate.aggregate(agg, COLLECTION, ItemUnwoundDto.class).getMappedResults();
    }

    public List<ItemUnwoundDto> getDetailsPerCategory(String category) {
        UnwindOperation unwind = unwind("items");
        MatchOperation match = match(Criteria.where("items.category").is(category));
        ProjectionOperation project = project("orderId")
                .and("items.name").as("itemName")
                .and("items.category").as("itemCategory")
                .and("items.price").as("itemPrice")
                .and("items.qty").as("itemQty");
        Aggregation agg = newAggregation(unwind, match, project);
        return mongoTemplate.aggregate(agg, COLLECTION, ItemUnwoundDto.class).getMappedResults();
    }

    public List<TotalQtySoldDto> getTotalQtySold() {
        UnwindOperation unwind = unwind("items");
        GroupOperation group = group("items.name").sum("items.qty").as("totalQuantitySold");
        Aggregation agg = newAggregation(unwind, group);
        return mongoTemplate.aggregate(agg, COLLECTION, TotalQtySoldDto.class).getMappedResults();
    }

    public List<ItemSubTotalDto> getItemSubTotal() {
        UnwindOperation unwind = unwind("items");
        ProjectionOperation project = project()
                .and("items.name").as("itemName")
                .and("items.price").as("price")
                .and("items.qty").as("quantity")
                .and(ArithmeticOperators.valueOf("items.qty")
                        .multiplyBy("items.price"))
                .as("itemSubTotal")
                .andExclude("_id");
        Aggregation agg = newAggregation(unwind, project);
        return mongoTemplate.aggregate(agg, COLLECTION, ItemSubTotalDto.class).getMappedResults();
    }

    public List<UnwoundOrderDto> getEmptyItems() {
        UnwindOperation unwind = unwind("items", true);
        Aggregation agg = newAggregation(unwind);
        return mongoTemplate.aggregate(agg, COLLECTION, UnwoundOrderDto.class).getMappedResults();
    }

    public List<RevenueByCityDto> getRevenueByCity() {
        GroupOperation group = group("customer.city")
                .sum("totalAmount").as("totalCityRevenue");
        Aggregation agg = newAggregation(group);
        return mongoTemplate.aggregate(agg, COLLECTION, RevenueByCityDto.class).getMappedResults();
    }

    public List<TotalQtySoldDto> getTotalQuantitySoldElectronics() {
        UnwindOperation unwind = unwind("items");
        MatchOperation match = match(Criteria.where("items.category").is("Electronics"));
        GroupOperation group = group("items.name")
                .sum("items.qty").as("totalQuantitySold");
        Aggregation agg = newAggregation(unwind, match, group);
        return mongoTemplate.aggregate(agg, COLLECTION, TotalQtySoldDto.class).getMappedResults();
    }

    public List<CategoryRevenueDto> getCategoryRevenue() {
        UnwindOperation unwind = unwind("items");
        GroupOperation group = group("items.category")
                .sum(ArithmeticOperators.valueOf("items.qty").multiplyBy("items.price"))
                .as("categoryRevenue");
        SortOperation sort = sort(Sort.Direction.DESC, "categoryRevenue");
        Aggregation agg = newAggregation(unwind, group, sort);
        return mongoTemplate.aggregate(agg, COLLECTION, CategoryRevenueDto.class).getMappedResults();
    }

    public List<CustomerSpentDto> getCustomerSpent() {
        GroupOperation group = group("customer.name")
                .sum("totalAmount").as("totalSpent");
        LimitOperation limit = limit(3);
        Aggregation agg = newAggregation(group, limit);
        return mongoTemplate.aggregate(agg, COLLECTION, CustomerSpentDto.class).getMappedResults();
    }

    public List<ItemsNameTotalQtySoldDto> getItemNameTotalQuantitySold() {
        UnwindOperation unwind = unwind("items");
        MatchOperation match = match(Criteria.where("items.qty").gte(2));
        GroupOperation group = group("items.name")
                .sum("items.qty").as("totalQuantitySold");
        SortOperation sort = sort(Sort.Direction.ASC, "totalQuantitySold");
        Aggregation agg = newAggregation(unwind, match, group, sort);
        return mongoTemplate.aggregate(agg, COLLECTION, ItemsNameTotalQtySoldDto.class).getMappedResults();
    }

    public BestSellingDto getBestSelling() {
        UnwindOperation unwind = unwind("items");
        GroupOperation group = group("items.name")
                .sum("items.qty").as("totalSold");
        SortOperation sort = sort(Sort.Direction.DESC,"totalSold");
        LimitOperation limit = limit(1);
        Aggregation agg = newAggregation(unwind, group, sort, limit);
        return mongoTemplate.aggregate(agg, COLLECTION, BestSellingDto.class).getUniqueMappedResult();
    }
}
