package com.esms.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esms.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
}
