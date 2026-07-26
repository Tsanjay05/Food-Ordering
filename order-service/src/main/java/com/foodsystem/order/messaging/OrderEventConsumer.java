package com.foodsystem.order.messaging;

import com.foodsystem.shared.event.OrderCreatedEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final RuntimeService runtimeService;

    @JmsListener(destination = OrderEventPublisher.ORDER_CREATED_QUEUE)
    public void consumeOrderCreated(OrderCreatedEvent event) {
        log.info("[OrderService] Order #{} - ActiveMQ Event Received. Starting Camunda process.", event.getOrderId());
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", event.getOrderId());
        variables.put("customerName", event.getCustomerName());
        variables.put("item", event.getItem());
        variables.put("amount", event.getAmount().doubleValue());
        
        runtimeService.startProcessInstanceByKey(
            "order-process", 
            String.valueOf(event.getOrderId()), 
            variables
        );
        
        log.info("[OrderService] Order #{} - Workflow started", event.getOrderId());
    }
}
