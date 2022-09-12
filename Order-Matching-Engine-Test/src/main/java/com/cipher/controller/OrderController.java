package com.cipher.controller;

import com.cipher.converter.Converter;
import com.cipher.dto.OrderDto;
import com.cipher.entities.Order;
import com.cipher.handler.CommandHandler;
import com.cipher.matching.engine.enums.OrderStatus;
import com.cipher.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class OrderController {

    @Autowired
    private CommandHandler commandHandler;

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/order")
    public ResponseEntity<?> placeOrder(@RequestBody OrderDto orderDto) {
        return ResponseEntity.ok(commandHandler.handlePlaceOrderCommand(Converter.convert(orderDto, Order.class)));
    }

    @PutMapping("/cancel/order/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        Optional<Order> optionalOrder = orderService.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (OrderStatus.CANCELLED.equals(order.getStatus()))
                return ResponseEntity.badRequest().body("Order already cancelled!");
            if (OrderStatus.EXECUTED.equals(order.getStatus()))
                return ResponseEntity.badRequest().body("Order already executed!");
            return ResponseEntity.ok(commandHandler.handleCancelOrderCommand(order));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/list/orders/{instrument}")
    public ResponseEntity<?> listOrders(@PathVariable String instrument) {
        return ResponseEntity.ok(commandHandler.handleListOrderCommand(instrument));
    }
}
