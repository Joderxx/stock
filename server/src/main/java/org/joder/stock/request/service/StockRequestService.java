package org.joder.stock.request.service;

import lombok.SneakyThrows;
import org.joder.stock.model.config.AppConfig;
import org.joder.stock.request.domain.StockApi;
import org.joder.stock.request.domain.StockQuery;
import org.joder.stock.request.domain.StockResponse;
import org.joder.stock.request.domain.StockResponseData;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对tushare网站获取的数据进行统一处理，并转换成期望的类
 *
 * @author Joder 2020/8/19 20:38
 */
@Service
public class StockRequestService {

    private AppConfig appConfig;
    private WebClient webClient;

    public StockRequestService(AppConfig appConfig, WebClient webClient) {
        this.appConfig = appConfig;
        this.webClient = webClient;
    }

    public <T> List<T> request(StockApi stockApi, Map<String, Object> params, Class<T> clazz) {
        StockQuery query = new StockQuery();
        query.setParams(params == null ? new HashMap<>() : params);
        List<String> names = fieldNames(clazz);
        query.setApiName(stockApi.getName());
        query.setFields(join(toHumpName(names)));
        query.setToken(appConfig.getToken());
        StockResponse ret = request(query);
        return parse(ret, clazz);
    }

    private List<String> fieldNames(Class clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<String> ret = new ArrayList<>();
        for (Field field : declaredFields) {
            ret.add(field.getName());
        }
        return ret;
    }

    private List<String> toHumpName(List<String> list) {
        return list.stream().map(this::toHumpName).collect(Collectors.toList());
    }

    private String toHumpName(String name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                sb.append('_').append((char) (c + 32));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String parseHumpName(String name) {
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_') {
                upper = true;
            } else if (upper) {
                sb.append((char) (c - 32));
                upper = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String join(List<String> list) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            if (first) {
                sb.append(str);
                first = false;
            } else {
                sb.append(",").append(str);
            }
        }
        return sb.toString();
    }

    private StockResponse request(StockQuery query) {
        return webClient.post()
                .uri(appConfig.getApi())
                .bodyValue(query)
                .retrieve().bodyToMono(StockResponse.class)
                .subscribeOn(Schedulers.single())
                .block();
    }

    private <T> List<T> parse(StockResponse stockResponse, Class<T> clazz) {
        if (stockResponse.getCode() != 0) {
            return new ArrayList<>();
        }
        StockResponseData data = stockResponse.getData();
        List<String> fields = data.getFields();
        Map<Integer, String> fieldMap = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            String s = fields.get(i);
            fieldMap.put(i, parseHumpName(s));
        }
        return data.getItems()
                .stream()
                .map(e -> this.parse(e, fieldMap, clazz))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private <T> T parse(List<Object> data, Map<Integer, String> fieldMap, Class<T> clazz) {
        T instance = clazz.getDeclaredConstructor().newInstance();
        for (int i = 0; i < data.size(); i++) {
            String name = fieldMap.get(i);
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, data.get(i));
        }
        return instance;
    }
}
