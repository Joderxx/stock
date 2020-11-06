package org.joder.stock.model.vo;

import lombok.Data;

/**
 * @author Joder 2020/10/31 21:55
 */
@Data
public class StockPotentialVO {

    private String stockCode;
    private String stockName;
    private String currDate;
    private Double curr;
    private Double max;
    private Double min;
    private Double mean;
    private Long diffMinDay;
    private Long diffMaxDay;

    private Double predMax;
    private Double predMin;
    private Double predMean;
    private Long predDiffMinDay;
    private Long predDiffMaxDay;
}
