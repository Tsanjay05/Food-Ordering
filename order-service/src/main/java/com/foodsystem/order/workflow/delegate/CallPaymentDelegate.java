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

@Component("callPaymentDelegate")
@RequiredArgsConstructor
@Slf4j
public class CallPaymentDelegate implements JavaDelegate {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.payment.url:http://localhost:8082/api/payments}")
    private String paymentServiceUrl;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        Double amount = (Double) execution.getVariable("amount");
        
        log.info("[OrderService] Order #{} - Invoking Payment Service for amount ${}", orderId, amount);
        
        // Update DB status to indicate we are processing payment
        updateOrderStatus(orderId, "PAYMENT");
        
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", orderId);
        request.put("amount", amount);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(paymentServiceUrl, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String status = (String) response.getBody().get("status");
                execution.setVariable("paymentStatus", status);
                log.info("[OrderService] Order #{} - Payment Service status: {}", orderId, status);
                
                if (!"SUCCESS".equalsIgnoreCase(status)) {
                    updateOrderStatus(orderId, "CANCELLED");
                }
            } else {
                execution.setVariable("paymentStatus", "FAILED");
                updateOrderStatus(orderId, "CANCELLED");
                log.warn("[OrderService] Order #{} - Payment Service invocation failed", orderId);
            }
        } catch (Exception e) {
            log.error("[OrderService] Order #{} - Error calling Payment Service: {}", orderId, e.getMessage());
            execution.setVariable("paymentStatus", "FAILED");
            updateOrderStatus(orderId, "CANCELLED");
        }
    }

    private void updateOrderStatus(Long orderId, String status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }
}
