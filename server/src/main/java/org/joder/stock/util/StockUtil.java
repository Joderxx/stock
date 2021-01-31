package org.joder.stock.util;

import org.joder.stock.model.entity.Stock;
import org.joder.stock.service.StockService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joder 2020/12/6 22:03
 */
@Component
public class StockUtil implements ApplicationContextAware {

    private static final Map<String, Stock> map = new HashMap<>();
    private static ApplicationContext applicationContext;

    public static Stock getStock(String tsCode) {
        return map.getOrDefault(tsCode, new Stock());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (StockUtil.applicationContext == null) {
            StockUtil.applicationContext = applicationContext;
        }
        init();
    }

    private void init() {
        StockService stockService = applicationContext.getBean(StockService.class);
        map.clear();
        stockService.getStockList("")
                .doOnNext(e -> {
                    map.put(e.getTsCode(), e);
                })
                .subscribe();
    }

}
