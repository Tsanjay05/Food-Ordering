package com.foodsystem.order.workflow.delegate;

import com.foodsystem.order.entity.Order;
import com.foodsystem.order.repository.OrderRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Value;

@Component("callDeliveryDelegate")
@RequiredArgsConstructor
@Slf4j
public class CallDeliveryDelegate implements JavaDelegate {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.delivery.url:http://localhost:8084/api/delivery}")
    private String deliveryServiceUrl;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        
        log.info("[OrderService] Order #{} - Invoking Delivery Service", orderId);
        
        // Update DB status to OUT_FOR_DELIVERY
        updateOrderStatus(orderId, "OUT_FOR_DELIVERY");
        
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", orderId);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(deliveryServiceUrl, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String status = (String) response.getBody().get("status");
                log.info("[OrderService] Order #{} - Delivery Service status: {}", orderId, status);
            } else {
                log.warn("[OrderService] Order #{} - Delivery Service invocation failed", orderId);
            }
        } catch (Exception e) {
            log.error("[OrderService] Order #{} - Error calling Delivery Service: {}", orderId, e.getMessage());
        }
    }

    private void updateOrderStatus(Long orderId, String status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }
}
