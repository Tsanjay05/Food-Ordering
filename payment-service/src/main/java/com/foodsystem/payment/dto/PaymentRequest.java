package com.foodsystem.payment.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentRequest {
    private Long orderId;
    private BigDecimal amount;
}
