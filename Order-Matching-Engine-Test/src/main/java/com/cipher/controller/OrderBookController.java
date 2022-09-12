package com.cipher.controller;

import com.cipher.services.OrderBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderBookController {

    @Autowired
    private OrderBookService orderBookService;

    @GetMapping("/order/book/{instrument}")
    public ResponseEntity<?> getOrderBook(@PathVariable String instrument) {
        return ResponseEntity.ok(orderBookService.getOrderBook(instrument));
    }
}
