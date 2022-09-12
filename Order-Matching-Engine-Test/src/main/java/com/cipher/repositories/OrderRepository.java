package com.cipher.repositories;

import com.cipher.entities.Order;
import com.cipher.matching.engine.enums.OrderStatus;
import com.cipher.matching.engine.enums.Side;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusInAndInstrument(List<OrderStatus> orderStatuses, String instrument);

}
