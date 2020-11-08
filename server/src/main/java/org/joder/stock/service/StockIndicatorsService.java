package org.joder.stock.service;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.joder.stock.core.util.JsonUtil;
import org.joder.stock.model.entity.StockIndicator;
import org.joder.stock.repository.StockIndicatorsRepository;
import org.joder.stock.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joder 2020/11/7 17:20
 */
@Service
@Slf4j
public class StockIndicatorsService {

    private final StockIndicatorsRepository stockIndicatorsRepository;
    private final StockRepository stockRepository;
    private final WebClient webClient;

    public StockIndicatorsService(StockIndicatorsRepository stockIndicatorsRepository, StockRepository stockRepository,
                                  WebClient webClient) {
        this.stockIndicatorsRepository = stockIndicatorsRepository;
        this.stockRepository = stockRepository;
        this.webClient = webClient;
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void reloadAll() {
        stockRepository.all(StrUtil.EMPTY)
                .subscribeOn(Schedulers.elastic())
                .flatMap(e -> reloadPe(e.getTsCode()))
                .onErrorContinue((err, obj) -> log.error("出现错误, {}", err.getMessage()))
                .subscribe();
    }

    public Mono<Boolean> reloadPe(String tsCode) {
        return Mono.just(getAll(tsCode))
                .doOnNext(list -> {
                    if (!list.isEmpty()) {
                        stockIndicatorsRepository.deleteAllByTsCode(list.get(0).getTsCode())
                                .then(stockIndicatorsRepository.saveAll(list).then())
                                .subscribe();
                        log.info("保存 {} 指标成功", list.get(0).getTsCode());
                    }
                })
                .map(e -> true)
                .onErrorReturn(false);
    }

    public Flux<StockIndicator> listIndicator(String tsCode, String startDate, String endDate) {
        return stockIndicatorsRepository.getAllByTsCodeAndDateBetween(tsCode, startDate, endDate);
    }

    private List<StockIndicator> getAll(String tsCode) {
        String content = webClient.get()
                .uri("https://eniu.com/chart/pea/" + tsCode)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        if (StrUtil.isEmpty(content)) {
            return new ArrayList<>();
        }
        Ret ret = JsonUtil.parse(content, Ret.class);
        List<StockIndicator> list = new ArrayList<>();
        for (int i = 0; i < ret.date.size(); i++) {
            StockIndicator item = new StockIndicator();
            item.setTsCode(tsCode);
            item.setDate(ret.date.get(i));
            item.setPe(ret.pe.get(i));
            item.setPrice(ret.price.get(i));
            list.add(item);
        }
        return list;
    }

    @Data
    static class Ret {
        private List<String> date;
        @JsonProperty("pe_ttm")
        private List<Double> pe;
        private List<Double> price;
    }
}
