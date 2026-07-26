package com.foodsystem.kitchen.repository;

import com.foodsystem.kitchen.entity.KitchenTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitchenRepository extends JpaRepository<KitchenTicket, Long> {
}
