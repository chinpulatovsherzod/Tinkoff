package com.example.tinkoff.service;

import com.example.tinkoff.dto.FigiesDto;
import com.example.tinkoff.dto.StockPriceDto;
import com.example.tinkoff.dto.StocksDto;
import com.example.tinkoff.dto.TickerDto;
import com.example.tinkoff.model.Stock;

public interface StockService {

    Stock getStockByTicker(String ticker);
    StocksDto getStockByTickers(TickerDto tickers);
    StockPriceDto getPrices(FigiesDto figiesDto);


}
