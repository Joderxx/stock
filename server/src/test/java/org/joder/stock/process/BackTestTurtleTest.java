package org.joder.stock.process;

import org.joder.stock.core.domain.ProcessQuery;
import org.joder.stock.core.domain.ProcessResult;
import org.joder.stock.core.domain.StockData;
import org.joder.stock.core.service.process.BackTestProcess;
import org.joder.stock.core.service.process.impl.DefaultBackTestProcessImpl;
import org.joder.stock.model.entity.StockHistory;
import org.joder.stock.repository.StockHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Joder 2020/8/15 15:42
 */
@SpringBootTest
class BackTestTurtleTest {

    @Autowired
    private StockHistoryRepository repository;
    private BackTestProcess backTestProcess = new DefaultBackTestProcessImpl();

    private List<StockHistory> stockList;
    private Map<String, Object> map;

    @BeforeEach
    void before() {
        String code = "sz000756";
        String startDate = "2018-01-01";
        String endDate = "2020-12-31";
        stockList = repository.getHistory(code, startDate, endDate).collectList().block();
        map = new HashMap<>();
        map.put("minDayNum", 10);
        map.put("maxDayNum", 20);
    }

    @Test
    void testTurtleStrategy() {
        ProcessQuery query = new ProcessQuery("turtle",
                stockList.stream().map(item -> new StockData(item.getDay(), item.getOpen(), item.getClose(), item.getHigh(), item.getLow(), item.getVolume()))
                        .collect(Collectors.toList())
        );
        query.setHyperParams(map);
        ProcessResult processResult = backTestProcess.doProcess(query);
        System.out.println(processResult);
    }

    @Test
    void testTurtlePlusStrategy() {
        ProcessQuery query = new ProcessQuery("turtle-plus",
                stockList.stream().map(item -> new StockData(item.getDay(), item.getOpen(), item.getClose(), item.getHigh(), item.getLow(), item.getVolume()))
                        .collect(Collectors.toList()));
        query.setHyperParams(map);
        ProcessResult processResult = backTestProcess.doProcess(query);
        System.out.println(processResult);
    }

    @Test
    void testMeanStrategy() {
        ProcessQuery query = new ProcessQuery("mean",
                stockList.stream().map(item -> new StockData(item.getDay(), item.getOpen(), item.getClose(), item.getHigh(), item.getLow(), item.getVolume()))
                        .collect(Collectors.toList()));
        query.setHyperParams(map);
        ProcessResult processResult = backTestProcess.doProcess(query);
        System.out.println(processResult);
    }
}
