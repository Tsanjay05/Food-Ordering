package com.foodsystem.kitchen.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "kitchen_tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String item;

    @Column(name = "ticket_status", nullable = false)
    private String ticketStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
