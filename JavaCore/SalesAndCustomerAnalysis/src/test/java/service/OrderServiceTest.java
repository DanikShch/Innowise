package service;

import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;
    private Customer customer1;
    private Customer customer2;
    private Customer customer3;
    private List<Order> orders;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
        customer1 = new Customer("customer_id_1", "Alex", "alex@gmail.com",
                LocalDateTime.now(), 18, "Paris");
        customer2 = new Customer("customer_id_2", "John", "john@gmail.com",
                LocalDateTime.now(), 25, "Minsk");
        customer3 = new Customer("customer_id_3", "Paul", "paul@gmail.com",
                LocalDateTime.now(), 25, "Tokyo");

        OrderItem laptop = new OrderItem("Laptop", 2, 1000.0, Category.ELECTRONICS);
        OrderItem mouse = new OrderItem("Mouse", 1, 25.0, Category.ELECTRONICS);
        OrderItem book = new OrderItem("Book", 1, 15.0, Category.BOOKS);

        Order order1 = new Order("ORDER1", LocalDateTime.now(), customer1, List.of(laptop, mouse), OrderStatus.DELIVERED);
        Order order2 = new Order("ORDER2", LocalDateTime.now(), customer2, List.of(laptop, book), OrderStatus.DELIVERED);
        Order order3 = new Order("ORDER3", LocalDateTime.now(), customer1, List.of(laptop), OrderStatus.CANCELLED);

        orders = List.of(order1, order2, order3);
    }

    @Test
    void testGetUniqueCities() {
        List<String> uniqueCities = orderService.getUniqueCities(orders);

        assertEquals(2, uniqueCities.size());
        assertTrue(uniqueCities.contains("Minsk"));
        assertTrue(uniqueCities.contains("Paris"));
    }

    @Test
    void testGetTotalIncome() {
        double totalIncome = orderService.getTotalIncome(orders);

        assertEquals(4040, totalIncome);
    }

    @Test
    void testGetMostPopularProduct() {
        Optional<String> mostPopularProduct = orderService.getMostPopularProduct(orders);

        assertTrue(mostPopularProduct.isPresent());
        assertEquals("Laptop", mostPopularProduct.get());
    }

    @Test
    void testGetAverageCheck() {
        double averageCheck = orderService.getAverageCheck(orders);

        assertEquals(2020, averageCheck);
    }

    @Test
    void testGetCustomersWithMoreThanFiveOrders() {
        List<Customer> customers = orderService.getCustomersWithMoreThanFiveOrders(orders);

        assertTrue(customers.isEmpty());

        OrderItem item = new OrderItem("Test Product", 1, 10.0, Category.ELECTRONICS);
        List<Order> manyOrders = List.of(
                new Order("ORDER4", LocalDateTime.now(), customer3, List.of(item), OrderStatus.DELIVERED),
                new Order("ORDER5", LocalDateTime.now(), customer3, List.of(item), OrderStatus.DELIVERED),
                new Order("ORDER6", LocalDateTime.now(), customer3, List.of(item), OrderStatus.DELIVERED),
                new Order("ORDER7", LocalDateTime.now(), customer3, List.of(item), OrderStatus.DELIVERED),
                new Order("ORDER8", LocalDateTime.now(), customer3, List.of(item), OrderStatus.DELIVERED),
                new Order("ORDER9", LocalDateTime.now(), customer3, List.of(item), OrderStatus.DELIVERED)
        );

        customers = orderService.getCustomersWithMoreThanFiveOrders(manyOrders);

        assertEquals(1, customers.size());
        assertEquals(customer3, customers.getFirst());

    }

    @Test
    void testEmptyList() {
        List<Order> emptyList = List.of();

        assertEquals(0, orderService.getUniqueCities(emptyList).size());
        assertEquals(0.0, orderService.getTotalIncome(emptyList));
        assertFalse(orderService.getMostPopularProduct(emptyList).isPresent());
        assertEquals(0.0, orderService.getAverageCheck(emptyList));
        assertEquals(0, orderService.getCustomersWithMoreThanFiveOrders(emptyList).size());
    }
}