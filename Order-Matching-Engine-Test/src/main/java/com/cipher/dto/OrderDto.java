package com.cipher.dto;

import com.cipher.matching.engine.enums.OrderStatus;
import com.cipher.matching.engine.enums.Side;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDto {

    private Long id;
    private String instrument;
    private BigDecimal qty;
    private BigDecimal remQty;
    private BigDecimal price;
    private Side side;
    private OrderStatus status = OrderStatus.OPEN;
}
