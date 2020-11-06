package org.joder.stock.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.joder.stock.core.StockStrategy;
import org.joder.stock.core.domain.ProcessQuery;
import org.joder.stock.core.domain.ProcessResult;
import org.joder.stock.core.domain.StockData;
import org.joder.stock.core.domain.TradeReturn;
import org.joder.stock.core.service.process.BackTestProcess;
import org.joder.stock.model.constant.StringConstant;
import org.joder.stock.model.entity.Stock;
import org.joder.stock.model.entity.StockReal;
import org.joder.stock.model.vo.BackTestResultVO;
import org.joder.stock.notify.NotifyService;
import org.joder.stock.notify.domain.Message;
import org.joder.stock.notify.domain.StockMessage;
import org.joder.stock.repository.StockHistoryRepository;
import org.joder.stock.repository.StockRealRepository;
import org.joder.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 定时监听股票价格
 *
 * @author Joder 2020/8/29 20:58
 */
@Service
public class StockNotifyService {

    private NotifyService notifyService;
    private StockRealRepository realRepository;
    private BackTestProcess backTestProcess;
    private StockHistoryRepository stockHistoryRepository;
    private StockRepository stockRepository;

    public StockNotifyService(NotifyService notifyService, StockRealRepository realRepository, BackTestProcess backTestProcess,
                              StockHistoryRepository stockHistoryRepository, StockRepository stockRepository) {
        this.notifyService = notifyService;
        this.realRepository = realRepository;
        this.backTestProcess = backTestProcess;
        this.stockHistoryRepository = stockHistoryRepository;
        this.stockRepository = stockRepository;
    }

    public Mono<Void> notifyStock() {
        return realRepository.findAll()
                .flatMap(this::dealAll)
                .reduce((x1, x2) -> {
                    List<StockMessage> list = new ArrayList<>(x1);
                    list.addAll(x2);
                    return this.distinct(list);
                })
                .flatMap(item -> {
                    item = new ArrayList<>(new HashSet<>(item));
                    item.sort(Comparator.comparing(StockMessage::getProfit).reversed());
                    Message<StockMessage> message = new Message<>();
                    message.setTitles(StockMessage.getTitleMap());
                    message.setDate(DateUtil.now());
                    message.setType(StringConstant.EMAIL_INFO);
                    message.setData(item);
                    notifyService.notifyMessage(message);
                    return Mono.empty();
                });
    }

    private Mono<List<StockMessage>> dealAll(StockReal e) {
        String start = DateUtil.formatDate(DateUtil.offset(new Date(), DateField.YEAR, -2));
        String end = DateUtil.formatDate(DateUtil.offsetDay(new Date(), -1));
        return stockRepository.findByTsCode(e.getTsCode()).flatMap(stock -> {
            if (StrUtil.compare(stock.getListDate(), start, true) < 0) {
                return Mono.just(new ArrayList<>());
            }
            return stockHistoryRepository.getHistory(e.getTsCode(), start, end)
                    .collectList()
                    .flatMap(t -> {
                        List<StockData> list = t.stream()
                                .map(item -> new StockData(item.getDay(), item.getOpen(), item.getClose(), item.getHigh(), item.getLow(), item.getVolume()))
                                .collect(Collectors.toList());
                        if (list.isEmpty()) {
                            return Mono.empty();
                        }
                        StockData stockHistory1 = list.get(0);
                        StockData stockHistory2 = list.get(list.size() - 1);
                        double normalProfit = (stockHistory2.getClose() - stockHistory1.getClose()) / stockHistory1.getClose();
                        StockData stockData = list.get(list.size() - 1);
                        String date = DateUtil.formatDate(new Date(e.getTime()));
                        if (stockData.getDay().equals(date)) {
                            list.set(list.size() - 1, new StockData(date, e.getOpen(), e.getCurrentPrice(), e.getHigh(), e.getLow(), e.getVolume()));
                        } else {
                            list.add(new StockData(date, e.getOpen(), e.getCurrentPrice(), e.getHigh(), e.getLow(), e.getVolume()));
                        }
                        return dealEach(e, stock, list, normalProfit);
                    });
        });
    }

    private Mono<List<StockMessage>> dealEach(StockReal e, Stock stock, List<StockData> list, double normalProfit) {
        return Flux.fromArray(StockStrategy.values())
                .map(strategy -> backTestProcess.predictLast(new ProcessQuery(strategy.getCode(), list)))
                .map(info -> {
                    if (info.getVolume() == 0) {
                        return new HashMap<>();
                    }
                    return Map.of(
                            "today", info,
                            "backTest", backTestProcess.doProcess(new ProcessQuery(info.getStrategyCode(), list))
                    );
                })
                .filter(CollectionUtil::isNotEmpty)
                .map(item -> {
                    TradeReturn info = (TradeReturn) item.get("today");
                    ProcessResult result = (ProcessResult) item.get("backTest");
                    return StockMessage.builder()
                            .stockCode(stock.getTsCode())
                            .stockName(stock.getName())
                            .strategyName(StockStrategy.parseStrategy(info.getStrategyCode()).getName())
                            .saleSuggest(info.isSale() ? StringConstant.SALE : StringConstant.BUY)
                            .currentPrice(e.getCurrentPrice())
                            .profit(NumberUtil.formatPercent(new BackTestResultVO(result).getProfit(), 2))
                            .normalProfit(NumberUtil.formatPercent(normalProfit, 2))
                            .build();
                })
                .filter(s -> !StrUtil.startWith(s.getProfit(), '-'))
                .collectList();
    }

    private List<StockMessage> distinct(List<StockMessage> list) {
        Map<String, StockMessage> map = new HashMap<>();
        list.forEach(e -> map.put(e.getStockCode(), e));
        return map.values().stream()
                .sorted(Comparator.comparing(StockMessage::getStockCode))
                .collect(Collectors.toList());
    }
}
