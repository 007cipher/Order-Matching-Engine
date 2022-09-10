package com.cipher.matching.engine.bean;

import com.cipher.matching.engine.enums.OrderStatus;
import com.cipher.matching.engine.enums.Side;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;
import org.redisson.api.annotation.RIndex;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@REntity
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    @RId
    private Long id;
    @RIndex
    private String instrument;
    private BigDecimal qty;
    private BigDecimal remQty;
    @RIndex
    private BigDecimal price;
    @RIndex
    private Side side = Side.BUY;
    @RIndex
    private OrderStatus status = OrderStatus.OPEN;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;

        Order order = (Order) o;

        if (!id.equals(order.id)) return false;
        if (!instrument.equals(order.instrument)) return false;
        if (!qty.equals(order.qty)) return false;
        if (!price.equals(order.price)) return false;
        return side == order.side;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + instrument.hashCode();
        result = 31 * result + qty.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + side.hashCode();
        return result;
    }
}
