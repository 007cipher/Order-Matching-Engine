package com.cipher.entities;

import com.cipher.matching.engine.enums.OrderStatus;
import com.cipher.matching.engine.enums.Side;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "ORDERS")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String instrument;
    private BigDecimal qty;
    private BigDecimal remQty;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private Side side = Side.BUY;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.OPEN;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return id.equals(order.id) && instrument.equals(order.instrument) && qty.equals(order.qty) && price.equals(order.price) && side == order.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, instrument, qty, price, side);
    }
}
