package com.cipher.services;

import com.cipher.bean.Order;
import com.cipher.config.RedisConfig;
import com.cipher.enums.OrderStatus;
import com.cipher.enums.Side;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.condition.Conditions;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Slf4j
public class OrderService {

    private final RLiveObjectService liveObjectService = RedisConfig.getLiveObjectService();

    public Order saveOrder(Order order) {
        return liveObjectService.merge(order);
    }

    public Order deleteOrder(Order order) {
        Order detachedOrder = liveObjectService.detach(order);
        liveObjectService.delete(order);
        return detachedOrder;
    }

    public Order detachOrder(Order order) {
        return liveObjectService.detach(order);
    }

    public Collection<Order> getOrders(String instrument, BigDecimal price, Side side, List<OrderStatus> orderStatuses) {
        return liveObjectService.find(Order.class, Conditions.and(Conditions.eq("instrument", instrument), Conditions.eq("price", price), Conditions.eq("side", side), Conditions.in("status", orderStatuses)));
    }
}
