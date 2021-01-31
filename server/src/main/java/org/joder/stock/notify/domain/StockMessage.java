package org.joder.stock.notify.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author Joder 2020/8/26 23:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMessage {
    private String stockCode;
    private String stockName;
    private Double currentPrice;
    private String strategyName;
    private String saleSuggest;
    private String profit;
    private String normalProfit;

    public static LinkedHashMap<String, String> getTitleMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("stockCode", "股票代号");
        map.put("stockName", "股票名称");
        map.put("strategyName", "策略名称");
        map.put("currentPrice", "当前价格");
        map.put("saleSuggest", "建议操作");
        map.put("profit", "过去两年该策略利润率");
        map.put("normalProfit", "过去两年对比利润");
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockMessage that = (StockMessage) o;
        return Objects.equals(stockName, that.stockName) &&
                Objects.equals(currentPrice, that.currentPrice) &&
                Objects.equals(strategyName, that.strategyName) &&
                Objects.equals(saleSuggest, that.saleSuggest) &&
                Objects.equals(profit, that.profit) &&
                Objects.equals(normalProfit, that.normalProfit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockName, currentPrice, strategyName, saleSuggest, profit, normalProfit);
    }
}
