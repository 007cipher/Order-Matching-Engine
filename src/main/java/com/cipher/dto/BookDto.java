package com.cipher.dto;

import com.cipher.bean.OrderBook;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Map<BigDecimal, OrderBook> buyBook;
    private Map<BigDecimal, OrderBook> sellBook;
}
