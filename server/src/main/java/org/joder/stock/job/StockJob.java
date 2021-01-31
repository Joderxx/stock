package org.joder.stock.job;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.joder.stock.model.dto.StockDTO;
import org.joder.stock.model.dto.StockHistoryDTO;
import org.joder.stock.model.entity.Stock;
import org.joder.stock.model.entity.StockHistory;
import org.joder.stock.repository.StockRepository;
import org.joder.stock.request.domain.StockApi;
import org.joder.stock.request.service.StockRequestService;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Joder 2020/8/19 22:51
 */
@Component
public class StockJob {

    private StockRequestService stockRequestService;
    private StockRepository stockRepository;
    private ReactiveMongoTemplate mongoTemplate;

    public StockJob(StockRequestService stockRequestService, StockRepository stockRepository, ReactiveMongoTemplate mongoTemplate) {
        this.stockRequestService = stockRequestService;
        this.stockRepository = stockRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 17点自动更新每天数据
     *
     * @throws IOException
     */
    @PostConstruct
    @Scheduled(cron = "0 0 17 * * ?")
    public void init() throws IOException, InterruptedException {
        List<String> days = days();
        String maxDay = null;
        if (!days.isEmpty()) {
            reloadStockList();
        }
        for (String day : days) {
            String d = reloadHistory(day);
            maxDay = StrUtil.compare(maxDay, d, true) > 0 ? maxDay : d;
        }
        if (maxDay != null) {
            saveDay(maxDay);
        }
    }

    private void reloadStockList() {
        List<Stock> list = stockRequestService.request(StockApi.STOCK_BASIC, null, StockDTO.class)
                .stream()
                .filter(e -> "L".equalsIgnoreCase(e.getListStatus()))
                .map(StockDTO::toStock)
                .sorted(Comparator.comparing(Stock::getTsCode))
                .collect(Collectors.toList());
        stockRepository.deleteAll()
                .then(stockRepository.saveAll(list).then())
                .subscribe();
    }

    private String reloadHistory(String day) {
        Map<String, Object> map = new HashMap<>();
        map.put("trade_date", day);
        List<StockHistory> list = stockRequestService.request(StockApi.DAILY, map, StockHistoryDTO.class)
                .stream()
                .map(StockHistoryDTO::toStockHistory)
                .sorted(Comparator.comparing(StockHistory::getCode))
                .collect(Collectors.toList());
        if (!list.isEmpty()) {
            mongoTemplate.remove(new Query(Criteria.where("day").is(DateUtil.formatDate(DateUtil.parse(day)))), StockHistory.class)
                    .then(Mono.empty().then(mongoTemplate.insertAll(list)
                            .collectList().then()))
                    .subscribe();
            return day;
        }
        return null;
    }

    private List<String> days() throws IOException {
        File file = new File("read.day");
        if (!file.exists()) {
            file.createNewFile();
        }
        InputStream streamSafe = new FileInputStream(file);
        byte[] bytes = streamSafe.readAllBytes();
        Date date;
        if (bytes.length == 0) {
            date = DateUtil.parse("20000101");
        } else {
            date = DateUtil.parse(new String(bytes));
        }
        String now = DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        int i = 1;
        String start;
        List<String> list = new ArrayList<>();
        while ((start = DateUtil.format(DateUtil.offsetDay(date, i++), DatePattern.PURE_DATE_PATTERN)).compareTo(now) <= 0) {
            int week = DateUtil.dayOfWeek(DateUtil.parse(start));
            if ((!start.equals(now) || DateUtil.date().hour(true) > 16) && week > 1 && week < 7) {
                list.add(start);
            }
        }
        return list;
    }

    private void saveDay(String maxDate) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("read.day");
        fileOutputStream.write(maxDate.getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
