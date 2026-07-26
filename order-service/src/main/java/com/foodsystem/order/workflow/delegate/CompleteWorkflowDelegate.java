package com.foodsystem.order.workflow.delegate;

import com.foodsystem.order.entity.Order;
import com.foodsystem.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("completeWorkflowDelegate")
@RequiredArgsConstructor
@Slf4j
public class CompleteWorkflowDelegate implements JavaDelegate {

    private final OrderRepository orderRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        String paymentStatus = (String) execution.getVariable("paymentStatus");
        
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
                order.setStatus("DELIVERED");
                orderRepository.save(order);
                log.info("[OrderService] Order #{} - Workflow COMPLETE", orderId);
            } else {
                order.setStatus("CANCELLED");
                orderRepository.save(order);
                log.info("[OrderService] Order #{} - CANCELLED", orderId);
            }
        } else {
            log.warn("[OrderService] Order #{} - Could not find order to complete workflow", orderId);
        }
    }
}
