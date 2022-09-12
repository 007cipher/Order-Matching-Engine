package com.cipher.services;

import com.cipher.entities.Order;
import com.cipher.matching.engine.enums.OrderStatus;
import com.cipher.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findOpenOrdersByInstrument(String instrument) {
        return orderRepository.findByStatusInAndInstrument(Arrays.asList(OrderStatus.OPEN, OrderStatus.PARTIALLY_EXECUTED), instrument);
    }
}
