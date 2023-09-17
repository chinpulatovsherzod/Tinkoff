package com.example.tinkoff.dto;


import com.example.tinkoff.model.Stock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StocksDto {
    List<Stock> stocks;
}
