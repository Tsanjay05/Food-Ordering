package com.foodsystem.kitchen.dto;

import lombok.Data;

@Data
public class KitchenRequest {
    private Long orderId;
    private String item;
}
