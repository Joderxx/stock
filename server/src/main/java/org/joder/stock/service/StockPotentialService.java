package org.joder.stock.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.joder.stock.model.entity.Stock;
import org.joder.stock.model.entity.StockHistory;
import org.joder.stock.model.query.StockPotentialQuery;
import org.joder.stock.model.vo.StockPotentialVO;
import org.joder.stock.repository.StockHistoryRepository;
import org.joder.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Joder 2020/10/31 21:54
 */
@Service
public class StockPotentialService {

    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;

    public StockPotentialService(StockRepository stockRepository, StockHistoryRepository stockHistoryRepository) {
        this.stockRepository = stockRepository;
        this.stockHistoryRepository = stockHistoryRepository;
    }

    public Flux<StockPotentialVO> findPotentialList(StockPotentialQuery query) {
        String start;
        String mid;
        String end;
        if (query.getPredict()) {
            if (StrUtil.isNotEmpty(query.getCurrDate())) {
                start = DateUtil.formatDate(DateUtil.offsetMonth(DateUtil.parseDate(query.getCurrDate()), -1 * (query.getMonth() + query.getPredictMonth())));
                mid = DateUtil.formatDate(DateUtil.offsetMonth(DateUtil.parseDate(query.getCurrDate()), -1 * query.getPredictMonth()));
                end = query.getCurrDate();
            } else {
                start = DateUtil.formatDate(DateUtil.offsetMonth(new Date(), -1 * (query.getMonth() + query.getPredictMonth())));
                mid = DateUtil.formatDate(DateUtil.offsetMonth(new Date(), -1 * query.getPredictMonth()));
                end = DateUtil.formatDate(DateUtil.yesterday());
            }
        } else {
            start = DateUtil.formatDate(DateUtil.offsetMonth(new Date(), -1 * query.getMonth()));
            mid = DateUtil.formatDate(DateUtil.yesterday());
            end = DateUtil.formatDate(DateUtil.yesterday());
        }
        Flux<StockPotentialVO> ret = stockRepository.getAllByBetweenDay(null, start)
                .flatMap(e -> this.parse(e, start, mid))
                .filter(Objects::nonNull);
        if (query.getPredict()) {
            return ret.flatMap(e -> this.parsePred(e, mid, end))
                    .filter(Objects::nonNull);
        } else {
            return ret;
        }
    }

    private Mono<StockPotentialVO> parse(Stock e, String startDate, String endDate) {
        StockPotentialVO vo = new StockPotentialVO();
        vo.setStockCode(e.getTsCode());
        vo.setStockName(e.getName());
        return stockHistoryRepository.getHistory(e.getTsCode(), startDate, endDate)
                .collectList()
                .flatMap(historyList -> {
                    if (historyList == null || historyList.isEmpty()) {
                        return Mono.empty();
                    }
                    vo.setCurrDate(historyList.get(historyList.size() - 1).getDay());
                    Result result = getResult(historyList, vo.getCurrDate());
                    vo.setCurr(historyList.get(historyList.size() - 1).getClose());
                    vo.setMax(result.max);
                    vo.setMin(result.min);
                    vo.setMean(result.mean);
                    vo.setDiffMaxDay(result.diffMax);
                    vo.setDiffMinDay(result.diffMin);
                    if (vo.getCurr() >= vo.getMax()) {
                        return Mono.empty();
                    }
                    return Mono.just(vo);
                });
    }

    private Mono<StockPotentialVO> parsePred(StockPotentialVO vo, String startDate, String endDate) {
        return stockHistoryRepository.getHistory(vo.getStockCode(), startDate, endDate)
                .collectList()
                .flatMap(historyList -> {
                    if (historyList == null || historyList.isEmpty()) {
                        return Mono.empty();
                    }
                    Result result = getResult(historyList, vo.getCurrDate());
                    vo.setPredMax(result.max);
                    vo.setPredMin(result.min);
                    vo.setPredMean(result.mean);
                    vo.setPredDiffMaxDay(result.diffMax);
                    vo.setPredDiffMinDay(result.diffMin);
                    return Mono.just(vo);
                });
    }

    private Result getResult(List<StockHistory> historyList, String currDate) {
        BigDecimal sum = new BigDecimal(0);
        long volume = 0;
        double max = historyList.get(historyList.size() - 1).getClose();
        double min = historyList.get(historyList.size() - 1).getClose();
        String maxDate = currDate;
        String minDate = currDate;
        for (StockHistory history : historyList) {
            sum = sum.add(new BigDecimal(history.getClose() * history.getVolume()));
            volume += history.getVolume();
            if (history.getClose() >= max) {
                max = history.getClose();
                maxDate = history.getDay();
            }
            if (history.getClose() <= min) {
                min = history.getClose();
                minDate = history.getDay();
            }
        }
        Result result = new Result();
        result.mean = sum.setScale(2, RoundingMode.HALF_UP).doubleValue() / volume;
        result.max = max;
        result.min = min;
        result.diffMax = DateUtil.betweenDay(DateUtil.parseDate(currDate), DateUtil.parseDate(maxDate), true);
        result.diffMin = DateUtil.betweenDay(DateUtil.parseDate(currDate), DateUtil.parseDate(minDate), true);
        return result;
    }

    private static class Result {
        Double mean;
        Double max;
        Long diffMax;
        Double min;
        Long diffMin;
    }
}
