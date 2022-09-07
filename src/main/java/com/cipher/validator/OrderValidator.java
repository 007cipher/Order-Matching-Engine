package com.cipher.validator;

import com.cipher.bean.Order;

import java.util.Objects;

public class OrderValidator {

    public static boolean validateOrder(Order order) {
        return Objects.nonNull(order.getId())  || Objects.nonNull(order.getInstrument())  || Objects.nonNull(order.getQty())  || Objects.nonNull(order.getRemQty())  ||
                Objects.nonNull(order.getPrice())  || Objects.nonNull(order.getSide()) ;
    }
}
