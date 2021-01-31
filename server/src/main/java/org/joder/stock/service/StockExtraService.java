package org.joder.stock.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.joder.stock.model.entity.StockExtra;
import org.joder.stock.model.vo.StockExtraVO;
import org.joder.stock.repository.StockExtraRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Joder 2020/12/6 15:15
 */
@Service
public class StockExtraService {

    private final StockExtraRepository stockExtraRepository;

    public StockExtraService(StockExtraRepository stockExtraRepository) {
        this.stockExtraRepository = stockExtraRepository;
    }

    public Flux<StockExtra> listStockExtra(String tsCode, Boolean attention) {
        if (StrUtil.isEmpty(tsCode) && attention == null) {
            return stockExtraRepository.findAll();
        } else if (StrUtil.isNotEmpty(tsCode)) {
            return stockExtraRepository.findAllByTsCode(tsCode);
        } else if (attention != null) {
            return stockExtraRepository.findAllByAttention(attention);
        }
        return Flux.empty();
    }

    public Mono<StockExtra> getStockExtra(String tsCode) {
        return stockExtraRepository.findByTsCode(tsCode);
    }

    public Mono<Boolean> insert(StockExtra stockExtra) {
        return stockExtraRepository.findByTsCode(stockExtra.getTsCode())
                .defaultIfEmpty(stockExtra)
                .flatMap(e -> {
                    e.setAttention(stockExtra.getAttention());
                    e.setStockType(stockExtra.getStockType());
                    return stockExtraRepository.save(e);
                })
                .map(e -> true)
                .onErrorReturn(false);
    }

    public Mono<Boolean> delete(String tsCode) {
        return stockExtraRepository.deleteByTsCode(tsCode)
                .map(e -> true)
                .onErrorReturn(false);
    }
}
