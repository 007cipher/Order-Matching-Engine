package com.cipher.repositories;

import com.cipher.entities.Order;
import com.cipher.enums.OrderStatus;
import com.cipher.enums.Side;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusIn(List<OrderStatus> orderStatuses);

    List<Order> findByPriceAndSideAndInstrument(BigDecimal price, Side side, String instrument);
}
