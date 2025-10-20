package service;

import model.Customer;
import model.Order;
import model.OrderItem;
import model.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderService {
    public List<String> getUniqueCities(List<Order> orders) {
        return orders.stream().map(order -> order.getCustomer().getCity()).distinct().toList();
    }

    public double getTotalIncome(List<Order> orders) {
        return orders.stream().filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(order -> order.getItems().stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum()).sum();
    }

    public Optional<String> getMostPopularProduct(List<Order> orders) {
        Map<String, Long> productSales = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(OrderItem::getProductName, Collectors.summingLong(OrderItem::getQuantity)));
        return productSales.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }

    public double getAverageCheck(List<Order> orders) {
        return orders.stream().filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(order -> order.getItems().stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum())
                .average().orElse(0);
    }

    public List<Customer> getCustomersWithMoreThanFiveOrders(List<Order> orders) {
        Map<Customer, Long> customerOrderCount = orders.stream()
                .collect(Collectors.groupingBy(Order::getCustomer, Collectors.counting()));
        return customerOrderCount.entrySet().stream().filter(entry -> entry.getValue() > 5)
                .map(Map.Entry::getKey).toList();
    }
}
