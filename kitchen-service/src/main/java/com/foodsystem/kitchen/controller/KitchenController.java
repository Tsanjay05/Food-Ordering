package com.foodsystem.kitchen.controller;

import com.foodsystem.kitchen.entity.KitchenTicket;
import com.foodsystem.kitchen.repository.KitchenRepository;
import com.foodsystem.kitchen.dto.KitchenRequest;
import com.foodsystem.kitchen.dto.KitchenResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
@Slf4j
public class KitchenController {

    private final KitchenRepository kitchenRepository;

    @PostMapping
    public ResponseEntity<KitchenResponse> createTicket(@RequestBody KitchenRequest request) {
        log.info("[KitchenService] Creating kitchen ticket for Order #{} with item: {}", 
            request.getOrderId(), request.getItem());
        
        KitchenTicket ticket = KitchenTicket.builder()
            .orderId(request.getOrderId())
            .item(request.getItem())
            .ticketStatus("READY")
            .build();
        
        ticket = kitchenRepository.save(ticket);
        
        // Log exactly as required: [KitchenService] Order #1 - Kitchen ticket created, preparing food... READY
        log.info("[KitchenService] Order #{} - Kitchen ticket created, preparing food... READY", request.getOrderId());
        
        KitchenResponse response = KitchenResponse.builder()
            .ticketId(ticket.getId())
            .orderId(ticket.getOrderId())
            .item(ticket.getItem())
            .status(ticket.getTicketStatus())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.ok(response);
    }
}
