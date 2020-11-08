package org.joder.stock.controller;

import org.joder.stock.model.entity.StockIndicator;
import org.joder.stock.service.StockIndicatorsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Joder 2020/11/7 21:31
 */
@RestController
@RequestMapping("/stock/indicator")
public class StockIndicatorsController {

    private final StockIndicatorsService stockIndicatorsService;

    public StockIndicatorsController(StockIndicatorsService stockIndicatorsService) {
        this.stockIndicatorsService = stockIndicatorsService;
    }

    @GetMapping("")
    public Flux<StockIndicator> listIndicator(String tsCode, String startDate, String endDate) {
        return stockIndicatorsService.listIndicator(tsCode, startDate, endDate);
    }

    @PostMapping("/reload")
    public Mono<Boolean> reloadPe(String tsCode) {
        return stockIndicatorsService.reloadPe(tsCode);
    }
}
