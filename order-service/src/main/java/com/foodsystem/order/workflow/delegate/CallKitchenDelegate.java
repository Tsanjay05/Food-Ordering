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

@Component("callKitchenDelegate")
@RequiredArgsConstructor
@Slf4j
public class CallKitchenDelegate implements JavaDelegate {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.kitchen.url:http://localhost:8083/api/kitchen}")
    private String kitchenServiceUrl;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        String item = (String) execution.getVariable("item");
        
        log.info("[OrderService] Order #{} - Invoking Kitchen Service for item: {}", orderId, item);
        
        // Update DB status to KITCHEN_PREP
        updateOrderStatus(orderId, "KITCHEN_PREP");
        
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", orderId);
        request.put("item", item);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(kitchenServiceUrl, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String status = (String) response.getBody().get("status");
                log.info("[OrderService] Order #{} - Kitchen Service status: {}", orderId, status);
            } else {
                log.warn("[OrderService] Order #{} - Kitchen Service invocation failed", orderId);
            }
        } catch (Exception e) {
            log.error("[OrderService] Order #{} - Error calling Kitchen Service: {}", orderId, e.getMessage());
        }
    }

    private void updateOrderStatus(Long orderId, String status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }
}
