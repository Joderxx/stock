package com.joder;

import cn.hutool.core.io.resource.ResourceUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.joder.stock.core.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Joder 2020/8/29 16:27
 */
class TestSplit {

    @Test
    void test1() {
        String str = "var hq_str_sh601006=\"大秦铁路,6.580,6.580,6.600,6.620,6.540,6.600,6.610,23594267,155455819.000,662375,6.600,405000,6.590,248900,6.580,212600,6.570,394000,6.560,30100,6.610,884440,6.620,797380,6.630,593400,6.640,806600,6.650,2020-08-28,15:00:00,00,\";\n\r" +
                "var hq_str_sh601006=\"大秦铁路1,6.580,6.580,6.600,6.620,6.540,6.600,6.610,23594267,155455819.000,662375,6.600,405000,6.590,248900,6.580,212600,6.570,394000,6.560,30100,6.610,884440,6.620,797380,6.630,593400,6.640,806600,6.650,2020-08-28,15:00:00,00,\";";
        Pattern pattern = Pattern.compile("var .*_.*_(.*)=\"(.*)\";");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.groupCount());
            System.out.println(matcher.group(1) + "\t" + matcher.group(2));
        }
    }

    @Test
    @SneakyThrows
    void test2() {
        String s = ResourceUtil.readStr("test", Charset.defaultCharset());
        System.out.println(parse(s));
    }

    @Test
    void test3() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://eniu.com/chart/pea/sh601311";
        String res = restTemplate.getForObject(url, String.class);
        System.out.println(parse(res));
    }

    @SneakyThrows
    private List<Item> parse(String s) {
        ObjectMapper mapper = JsonUtil.getMapper();
        Ret ret = mapper.readValue(s, Ret.class);
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < ret.date.size(); i++) {
            Item item = new Item();
            item.setDate(ret.date.get(i));
            item.setPe(ret.pe.get(i));
            item.setPrice(ret.price.get(i));
            list.add(item);
        }
        return list;
    }

    @Data
    static class Item {
        private String date;
        private Double pe;
        private Double price;
    }

    @Data
    static class Ret {
        private List<String> date;
        @JsonProperty("pe_ttm")
        private List<Double> pe;
        private List<Double> price;
    }
}
