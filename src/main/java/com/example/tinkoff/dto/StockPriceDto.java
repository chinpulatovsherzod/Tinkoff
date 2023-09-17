package com.example.tinkoff.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@AllArgsConstructor
@Value
public class StockPriceDto {
    private List<StockPrice> prices;


}
