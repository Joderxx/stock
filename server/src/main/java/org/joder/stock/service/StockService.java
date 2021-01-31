package org.joder.stock.service;

import org.joder.stock.model.entity.Stock;
import org.joder.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Joder 2020/8/8 21:29
 */
@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Flux<Stock> getStockList(String code) {
        return stockRepository.all(code);
    }

    public Mono<Stock> getStock(String code) {
        return stockRepository.findByTsCode(code);
    }
}
