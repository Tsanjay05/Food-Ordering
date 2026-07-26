package com.foodsystem.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.foodsystem.payment.dto.PaymentRequest;
import com.foodsystem.payment.dto.PaymentResponse;
import com.foodsystem.payment.entity.Payment;
import com.foodsystem.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        paymentController = new PaymentController(paymentRepository);
    }

    @Test
    void testProcessPayment_Success() {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);
        request.setAmount(BigDecimal.valueOf(18.50));

        Payment payment = Payment.builder()
            .id(10L)
            .orderId(1L)
            .amount(BigDecimal.valueOf(18.50))
            .status("SUCCESS")
            .transactionId("TXN-12345")
            .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.processPayment(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("SUCCESS", response.getBody().getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_Failure_DivisibleBy5() {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(5L); // 5 % 5 == 0 -> should fail
        request.setAmount(BigDecimal.valueOf(18.50));

        Payment payment = Payment.builder()
            .id(11L)
            .orderId(5L)
            .amount(BigDecimal.valueOf(18.50))
            .status("FAILED")
            .transactionId("TXN-55555")
            .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.processPayment(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FAILED", response.getBody().getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}
