package com.foodsystem.payment.controller;

import com.foodsystem.payment.entity.Payment;
import com.foodsystem.payment.repository.PaymentRepository;
import com.foodsystem.payment.dto.PaymentRequest;
import com.foodsystem.payment.dto.PaymentResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        log.info("[PaymentService] Processing payment request for Order #{} with amount ${}", 
            request.getOrderId(), request.getAmount());
        
        // Mock success/failure calculation. Fail if orderId is divisible by 5
        String status = "SUCCESS";
        if (request.getOrderId() != null && request.getOrderId() % 5 == 0) {
            status = "FAILED";
        }
        
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Payment payment = Payment.builder()
            .orderId(request.getOrderId())
            .amount(request.getAmount())
            .status(status)
            .transactionId(transactionId)
            .build();
        
        payment = paymentRepository.save(payment);
        
        log.info("[PaymentService] Order #{} - Payment {}", request.getOrderId(), status);
        
        PaymentResponse response = PaymentResponse.builder()
            .paymentId(payment.getId())
            .orderId(payment.getOrderId())
            .amount(payment.getAmount())
            .status(payment.getStatus())
            .transactionId(payment.getTransactionId())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.ok(response);
    }
}
