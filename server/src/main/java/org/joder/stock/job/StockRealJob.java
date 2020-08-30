package org.joder.stock.job;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import org.joder.stock.model.entity.Stock;
import org.joder.stock.repository.StockRealRepository;
import org.joder.stock.repository.StockRepository;
import org.joder.stock.request.domain.StockRealData;
import org.joder.stock.request.service.StockRealDataRequestService;
import org.joder.stock.service.StockNotifyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

/**
 * @author Joder 2020/8/29 18:33
 */
@Component
public class StockRealJob {

    private StockRepository stockRepository;
    private StockRealDataRequestService stockRealDataRequestService;
    private StockRealRepository stockRealRepository;
    private StockNotifyService stockNotifyService;

    public StockRealJob(StockRepository stockRepository, StockRealDataRequestService stockRealDataRequestService,
                        StockRealRepository stockRealRepository, StockNotifyService stockNotifyService) {
        this.stockRepository = stockRepository;
        this.stockRealDataRequestService = stockRealDataRequestService;
        this.stockRealRepository = stockRealRepository;
        this.stockNotifyService = stockNotifyService;
    }

    @PostConstruct
    public void init() {
        DateTime dateTime = new DateTime();
        int field = dateTime.getField(DateField.DAY_OF_WEEK);
        if (field == 1 || field == 7) {
            run();
        }
    }


    @Scheduled(cron = "0 35 9-15 ? * 2-6 *")
    public void run() {
        stockRepository.findAll()
                .collectList()
                .map(e -> e.stream().map(Stock::getTsCode).collect(Collectors.toList()))
                .map(e -> stockRealDataRequestService.request(e))
                .map(e -> e.stream().map(StockRealData::toStockReal).collect(Collectors.toList()))
                .flatMap(e -> stockRealRepository.saveAll(e).collectList())
                .then(stockNotifyService.notifyStock())
                .subscribe();
    }
}
