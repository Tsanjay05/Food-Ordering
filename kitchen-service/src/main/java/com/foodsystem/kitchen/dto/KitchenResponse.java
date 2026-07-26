package com.foodsystem.kitchen.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KitchenResponse {
    private Long ticketId;
    private Long orderId;
    private String item;
    private String status;
    private LocalDateTime timestamp;
}
