package com.example.tinkoff.service;

import com.example.tinkoff.dto.*;
import com.example.tinkoff.exception.StockNotFoundException;
import com.example.tinkoff.model.Currency;
import com.example.tinkoff.model.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrumentList;
import ru.tinkoff.invest.openapi.model.rest.Orderbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TinkoffService implements StockService{

    private final OpenApi openApi;

    @Async
    public CompletableFuture<MarketInstrumentList> getMarketInstrumentalTicker(String ticker){
        var context = openApi.getMarketContext();
        return context.searchMarketInstrumentsByTicker(ticker);
    }

    @Override
    public Stock getStockByTicker(String ticker) {
       var cf = getMarketInstrumentalTicker(ticker);
       var list = cf.join().getInstruments();
       if (list.isEmpty()){
           throw new StockNotFoundException(String.format("Stock %S not found", ticker));
       }

       var item = list.get(0);
       return new Stock(
               item.getTicker(),
               item.getFigi(),
               item.getName(),
               item.getType().getValue(),
               Currency.valueOf(item.getCurrency().getValue()),
               "TINKOFF");
    }


    @Override
    public StocksDto getStockByTickers(TickerDto tickers){
        List<CompletableFuture<MarketInstrumentList>> marketInstruments = new ArrayList<>();
        tickers.getTickers().forEach(ticker -> marketInstruments.add(getMarketInstrumentalTicker(ticker)));
        List<Stock> stocks = marketInstruments.stream()
                .map(CompletableFuture::join)
                .map(mi -> {
                    if (!mi.getInstruments().isEmpty()){
                        return mi.getInstruments().get(0);
                    }
                    return null;
                })
                .filter(el -> Objects.nonNull(el))
                .map(mi -> new Stock(
                        mi.getTicker(),
                        mi.getFigi(),
                        mi.getName(),
                        mi.getType().getValue(),
                        Currency.valueOf(mi.getCurrency().getValue()),
                        "TINKOFF"
                ))
                .collect(Collectors.toList());

        return new StocksDto(stocks);
    }

    @Async
    public CompletableFuture<Optional<Orderbook>> getOrderBookByFigi(String figi){
        var orderBook = openApi.getMarketContext().getMarketOrderbook(figi, 0);
        log.info("Getting price {} from Tinkoff", figi);
        return orderBook;
    }

    @Override
    public StockPriceDto getPrices(FigiesDto figiesDto){
        long start = System.currentTimeMillis();
        List<CompletableFuture<Optional<Orderbook>>> orderBooks = new ArrayList<>();
        figiesDto.getFigies().forEach(figi -> orderBooks.add(getOrderBookByFigi(figi)));
        var listPrices =  orderBooks.stream()
                .map(CompletableFuture::join)
                .map(oo -> oo.orElseThrow(() -> new StockNotFoundException("Stock Not Found")))
                .map(orderbook -> new StockPrice(
                        orderbook.getFigi(),
                        orderbook.getLastPrice().doubleValue()
                ))
                .collect(Collectors.toList());

        log.info("Time {}", System.currentTimeMillis() - start);
        return new  StockPriceDto(listPrices);
    }
}
