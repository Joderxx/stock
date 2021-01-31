package org.joder.stock.notify.impl;

import cn.hutool.core.date.DateUtil;
import org.joder.stock.model.constant.StringConstant;
import org.joder.stock.notify.NotifyService;
import org.joder.stock.notify.domain.Message;
import org.joder.stock.notify.domain.StockMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * @author Joder 2020/8/26 23:59
 */
@SpringBootTest
@ActiveProfiles("dev")
class EmailNotifyServiceImplTest {

    @Autowired
    private NotifyService notifyService;

    @Test
    void notifyMessage() throws Exception {
        StockMessage stockMessage = StockMessage.builder()
                .currentPrice(9.41)
                .saleSuggest("买")
                .strategyName("海龟策略")
                .stockName("平安银行")
                .build();
        Message message = new Message();
        message.setDate(DateUtil.now());
        message.setType(StringConstant.EMAIL_INFO);
        message.setTitles(StockMessage.getTitleMap());
        message.setData(Arrays.asList(stockMessage));
        message.setExtend("hello");
        Assert.isTrue(notifyService.notifyMessage(message), "错误");
    }
}