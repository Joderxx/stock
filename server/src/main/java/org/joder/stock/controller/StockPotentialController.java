package org.joder.stock.controller;

import org.joder.stock.model.query.StockPotentialQuery;
import org.joder.stock.model.vo.StockPotentialVO;
import org.joder.stock.service.StockPotentialService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author Joder 2020/10/31 22:33
 */
@RestController
public class StockPotentialController {

    private final StockPotentialService stockPotentialService;

    public StockPotentialController(StockPotentialService stockPotentialService) {
        this.stockPotentialService = stockPotentialService;
    }

    @GetMapping("/stock/potential")
    public Flux<StockPotentialVO> listData(StockPotentialQuery query) {
        return stockPotentialService.findPotentialList(query);
    }
}
