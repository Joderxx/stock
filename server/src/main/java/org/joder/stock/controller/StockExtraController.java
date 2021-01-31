package org.joder.stock.controller;

import org.joder.stock.model.entity.StockExtra;
import org.joder.stock.model.vo.StockExtraVO;
import org.joder.stock.service.StockExtraService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Joder 2020/12/6 19:32
 */
@RestController
@RequestMapping("/stock/attention")
public class StockExtraController {

    private final StockExtraService stockExtraService;

    public StockExtraController(StockExtraService stockExtraService) {
        this.stockExtraService = stockExtraService;
    }

    @GetMapping
    public Flux<StockExtraVO> listStockExtra(String tsCode, Boolean attention) {
        return stockExtraService.listStockExtra(tsCode, attention)
                .map(StockExtraVO::new);
    }

    @GetMapping("/{tsCode}")
    public Mono<StockExtraVO> getStockExtra(@PathVariable String tsCode) {
        return stockExtraService.getStockExtra(tsCode).map(StockExtraVO::new);
    }

    @PostMapping
    public Mono<Boolean> insert(@RequestBody StockExtraVO stockExtra) {
        return stockExtraService.insert(stockExtra.to());
    }

    @DeleteMapping("/{tsCode}")
    public Mono<Boolean> delete(@PathVariable String tsCode) {
        return stockExtraService.delete(tsCode);
    }
}
