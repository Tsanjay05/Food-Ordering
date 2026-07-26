package com.foodsystem.delivery.controller;

import com.foodsystem.delivery.entity.Delivery;
import com.foodsystem.delivery.repository.DeliveryRepository;
import com.foodsystem.delivery.dto.DeliveryRequest;
import com.foodsystem.delivery.dto.DeliveryResponse;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;
    private final String[] DRIVERS = {"Sprint Runner", "Flash Bolt", "Speedy Gonzales", "Courier Express"};

    @PostMapping
    public ResponseEntity<DeliveryResponse> assignDelivery(@RequestBody DeliveryRequest request) {
        log.info("[DeliveryService] Assigning driver for Order #{}", request.getOrderId());
        
        String driverName = DRIVERS[new Random().nextInt(DRIVERS.length)];
        
        Delivery delivery = Delivery.builder()
            .orderId(request.getOrderId())
            .driverName(driverName)
            .status("DELIVERED")
            .deliveredAt(LocalDateTime.now())
            .build();
        
        delivery = deliveryRepository.save(delivery);
        
        // Log exactly as required: [DeliveryService] Order #1 - Driver assigned, delivering... DELIVERED
        log.info("[DeliveryService] Order #{} - Driver assigned, delivering... DELIVERED", request.getOrderId());
        
        DeliveryResponse response = DeliveryResponse.builder()
            .deliveryId(delivery.getId())
            .orderId(delivery.getOrderId())
            .driverName(delivery.getDriverName())
            .status(delivery.getStatus())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.ok(response);
    }
}
