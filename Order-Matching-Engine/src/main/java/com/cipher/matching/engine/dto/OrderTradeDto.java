package com.cipher.matching.engine.dto;

import com.cipher.matching.engine.bean.Order;
import com.cipher.matching.engine.bean.Trade;
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