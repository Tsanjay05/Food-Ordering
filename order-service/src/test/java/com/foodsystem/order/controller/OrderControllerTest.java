package com.foodsystem.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.foodsystem.order.dto.OrderCreateRequest;
import com.foodsystem.order.dto.OrderResponse;
import com.foodsystem.order.entity.Order;
import com.foodsystem.order.mapper.OrderMapper;
import com.foodsystem.order.repository.OrderRepository;
import com.foodsystem.order.messaging.OrderEventPublisher;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderController = new OrderController(orderRepository, orderMapper, orderEventPublisher);
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        OrderCreateRequest request = new OrderCreateRequest();
        request.setCustomerName("Alice");
        request.setItem("Burger");
        request.setAmount(BigDecimal.valueOf(18.50));

        Order order = new Order();
        order.setId(1L);
        order.setCustomerName("Alice");
        order.setItem("Burger");
        order.setAmount(BigDecimal.valueOf(18.50));
        order.setStatus("PLACED");

        OrderResponse responseDto = new OrderResponse();
        responseDto.setOrderId(1L);
        responseDto.setCustomerName("Alice");
        responseDto.setItem("Burger");
        responseDto.setAmount(BigDecimal.valueOf(18.50));
        responseDto.setStatus("PLACED");

        when(orderMapper.toEntity(any(OrderCreateRequest.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(responseDto);

        // Act
        ResponseEntity<OrderResponse> response = orderController.createOrder(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Alice", response.getBody().getCustomerName());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderEventPublisher, times(1)).publishOrderCreated(any());
    }
}
