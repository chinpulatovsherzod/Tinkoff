package com.example.tinkoff.controller;

import com.example.tinkoff.dto.FigiesDto;
import com.example.tinkoff.dto.StockPriceDto;
import com.example.tinkoff.dto.StocksDto;
import com.example.tinkoff.dto.TickerDto;
import com.example.tinkoff.model.Stock;
import com.example.tinkoff.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/stocks/{ticker}")
    public Stock getStock(@PathVariable String ticker){
        return stockService.getStockByTicker(ticker);
    }

    @PostMapping("/stocks/getStockByTickers")
    public StocksDto getStockByTickers(@RequestBody TickerDto tickerDto){
       return stockService.getStockByTickers(tickerDto);
    }

    @PostMapping("/prices")
    public StockPriceDto getPrices(@RequestBody FigiesDto figiesDto){
       return stockService.getPrices(figiesDto);
    }

}
