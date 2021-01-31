package org.joder.stock.model.vo;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joder.stock.core.util.JsonUtil;
import org.joder.stock.model.entity.StockExtra;
import org.joder.stock.util.StockUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joder 2020/12/6 21:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockExtraVO {

    private String tsCode;
    private String stockName;
    private List<String> stockType;
    private Boolean attention;

    public StockExtraVO(StockExtra stockExtra) {
        tsCode = stockExtra.getTsCode();
        stockType = JsonUtil.parse(stockExtra.getStockType(), new TypeReference<ArrayList<String>>() {
        });
        attention = stockExtra.getAttention();
        stockName = StockUtil.getStock(tsCode).getName();
    }

    public StockExtra to() {
        StockExtra stockExtra = new StockExtra();
        stockExtra.setAttention(attention);
        stockExtra.setTsCode(tsCode);
        stockExtra.setStockType(JsonUtil.toJson(stockType));
        return stockExtra;
    }
}
