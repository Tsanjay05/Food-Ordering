package com.foodsystem.order.messaging;

import com.foodsystem.shared.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final JmsTemplate jmsTemplate;
    
    public static final String ORDER_CREATED_QUEUE = "order.created";

    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("[OrderService] Order #{} - Publishing order created event to ActiveMQ", event.getOrderId());
        jmsTemplate.convertAndSend(ORDER_CREATED_QUEUE, event);
    }
}
