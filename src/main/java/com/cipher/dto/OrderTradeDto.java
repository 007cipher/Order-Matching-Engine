package com.cipher.dto;

import com.cipher.bean.Order;
import com.cipher.bean.Trade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTradeDto {

    private Order order;
    private List<Order> matchedOrder;
    private List<Trade> trade;
}