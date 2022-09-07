package com.cipher.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade implements Serializable {

    private Long id;
    private Long orderId;
    private Long againstOrderId;
    private String instrument;
    private BigDecimal qty;
    private BigDecimal price;
}