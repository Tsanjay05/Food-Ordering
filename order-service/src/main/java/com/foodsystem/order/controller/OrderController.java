package com.foodsystem.order.controller;

import com.foodsystem.order.dto.OrderCreateRequest;
import com.foodsystem.order.dto.OrderResponse;
import com.foodsystem.order.entity.Order;
import com.foodsystem.order.mapper.OrderMapper;
import com.foodsystem.order.repository.OrderRepository;
import com.foodsystem.order.messaging.OrderEventPublisher;
import com.foodsystem.shared.event.OrderCreatedEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderEventPublisher orderEventPublisher;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        log.info("[OrderService] Received request to place order for customer: {}, item: {}, amount: ${}", 
            request.getCustomerName(), request.getItem(), request.getAmount());
        
        Order order = orderMapper.toEntity(request);
        order.setStatus("PLACED");
        order = orderRepository.save(order);
        
        log.info("[OrderService] Order #{} - PLACED", order.getId());

        // Publish to ActiveMQ
        OrderCreatedEvent event = OrderCreatedEvent.builder()
            .orderId(order.getId())
            .customerName(order.getCustomerName())
            .item(order.getItem())
            .amount(order.getAmount())
            .build();
        
        orderEventPublisher.publishOrderCreated(event);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toResponse(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> responses = orders.stream()
            .map(orderMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("id") Long id) {
        return orderRepository.findById(id)
            .map(orderMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
