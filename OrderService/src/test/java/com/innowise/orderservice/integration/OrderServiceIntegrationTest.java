package com.innowise.orderservice.integration;

import com.innowise.orderservice.client.UserServiceClient;
import com.innowise.orderservice.dto.request.OrderItemRequestDto;
import com.innowise.orderservice.dto.request.OrderRequestDto;
import com.innowise.orderservice.dto.response.OrderResponseDto;
import com.innowise.orderservice.dto.response.UserResponseDto;
import com.innowise.orderservice.model.Item;
import com.innowise.orderservice.model.OrderStatus;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class OrderServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemRepository itemRepository;

    @MockBean
    private UserServiceClient userServiceClient;

    @Test
    void createOrder_Success() {
        var item = new Item();
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(10));
        itemRepository.save(item);

        UserResponseDto currentUser = UserResponseDto.builder()
                .id(1L)
                .email("john@example.com")
                .name("John")
                .surname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(userServiceClient.getCurrentUser()).thenReturn(currentUser);

        OrderRequestDto request = new OrderRequestDto();
        var itemDto = new OrderItemRequestDto();
        itemDto.setItemId(1L);
        itemDto.setQuantity(2L);
        request.setItems(List.of(itemDto));

        OrderResponseDto response = orderService.createOrder(request);

        assertNotNull(response.getId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(1, response.getItems().size());
        assertEquals(2L, response.getItems().getFirst().getQuantity());

        verify(userServiceClient).getCurrentUser();
    }
}
