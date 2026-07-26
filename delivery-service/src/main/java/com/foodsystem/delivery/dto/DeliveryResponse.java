package com.foodsystem.delivery.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryResponse {
    private Long deliveryId;
    private Long orderId;
    private String driverName;
    private String status;
    private LocalDateTime timestamp;
}
