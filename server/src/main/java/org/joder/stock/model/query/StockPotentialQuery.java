package org.joder.stock.model.query;

import lombok.Data;

/**
 * @author Joder 2020/11/1 11:57
 */
@Data
public class StockPotentialQuery {
    private Integer month;
    private Integer predictMonth;
    private String currDate;

    private Boolean predict;

}
