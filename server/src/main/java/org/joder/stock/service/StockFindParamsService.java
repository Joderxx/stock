package org.joder.stock.service;

import org.joder.stock.core.util.JsonUtil;
import org.joder.stock.model.entity.BestParam;
import org.joder.stock.model.query.BackTestQuery;
import org.joder.stock.model.query.BestParamQuery;
import org.joder.stock.repository.BestParamRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Joder 2020/8/25 18:23
 */
@Service
public class StockFindParamsService implements InitializingBean {

    private BestParamRepository bestParamRepository;
    private StockBackTestService stockBackTestService;

    public StockFindParamsService(BestParamRepository bestParamRepository, StockBackTestService stockBackTestService) {
        this.bestParamRepository = bestParamRepository;
        this.stockBackTestService = stockBackTestService;
    }

    public Flux<BestParam> getBestParams() {
        return bestParamRepository.findAll(Sort.sort(BestParam.class).by(BestParam::getCreateTime).descending());
    }

    public Mono<Boolean> runFindParam(BestParamQuery query) {
        bestParamRepository.save(
                BestParam.builder()
                        .tsCode(query.getTsCode())
                        .strategyCode(query.getStrategyCode())
                        .params(JsonUtil.toJson(query.getHyperParams()))
                        .initMoney(query.getInitMoney())
                        .startDate(query.getStartDate())
                        .endDate(query.getEndDate())
                        .state(0)
                        .createTime(System.currentTimeMillis())
                        .build()
        ).flatMap(bestParam ->
                Flux.fromIterable(generateSequence(query))
                        .flatMap(e -> stockBackTestService.backTest(e))
                        .reduce((x1, x2) -> compare(x1.getProfit(), x2.getProfit()) >= 0 ? x1 : x2)
                        .map(e -> {
                            bestParam.setBestParams(JsonUtil.toJson(e.getQuery().getHyperParams()));
                            bestParam.setProfit(e.getProfit());
                            bestParam.setState(1);
                            return bestParam;
                        })
                        .flatMap(e -> bestParamRepository.save(bestParam))
                        .subscribeOn(Schedulers.elastic())
        ).subscribeOn(Schedulers.elastic())
                .subscribe();
        return Mono.just(true);
    }

    private List<BackTestQuery> generateSequence(BestParamQuery query) {
        Map<String, Object[]> hyperParams = query.getHyperParams();
        Set<Map.Entry<String, Object[]>> entries = hyperParams.entrySet();
        Map<String, List<Object>> map = new HashMap<>();
        for (Map.Entry<String, Object[]> entry : entries) {
            Object[] value = entry.getValue();
            map.put(entry.getKey(), generateSequence(value));
        }
        List<Map<String, Object>> ls = new ArrayList<>();
        generateSequence(map, ls, new HashMap<>(), new ArrayList<>(hyperParams.keySet()), 0);
        return ls.stream()
                .map(e ->
                        BackTestQuery.builder()
                                .hyperParams(e)
                                .startDate(query.getStartDate())
                                .endDate(query.getEndDate())
                                .strategyCode(query.getStrategyCode())
                                .initMoney(query.getInitMoney())
                                .stockCode(query.getTsCode())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private void generateSequence(Map<String, List<Object>> hyperParams, List<Map<String, Object>> list, Map<String, Object> params,
                                  List<String> keyArr, int index) {
        if (index >= keyArr.size()) {
            list.add(new HashMap<>(params));
        } else {
            String key = keyArr.get(index);
            List<Object> ls = hyperParams.get(key);
            for (Object object : ls) {
                params.put(key, object);
                generateSequence(hyperParams, list, params, keyArr, index + 1);
                params.remove(key);
            }
        }
    }

    private int compare(Double d1, Double d2) {
        if (d1 == null) {
            return -1;
        } else if (d2 == null) {
            return 1;
        } else {
            return Double.compare(d1, d2);
        }
    }

    private List<Object> generateSequence(Object[] arr) {
        if (arr == null || arr.length == 0) {
            return new ArrayList<>(0);
        }
        List<Object> list = new ArrayList<>();
        if (arr.length == 1) {
            list.add(arr[0]);
        } else {
            if (arr[0] instanceof Double && arr[1] instanceof Double) {
                Double start = (Double) arr[0];
                Double end = (Double) arr[1];
                Double interval = (Double) arr[2];
                for (double i = start; i <= end; i += interval) {
                    list.add(i);
                }
                if (list.get(list.size() - 1) != end) {
                    list.add(end);
                }
            } else if (arr[0] instanceof Integer && arr[1] instanceof Integer) {
                Integer start = (Integer) arr[0];
                Integer end = (Integer) arr[1];
                Integer interval = (Integer) arr[2];
                for (int i = start; i <= end; i += interval) {
                    list.add(i);
                }
            } else {
                list.addAll(Arrays.asList(arr));
            }
        }
        return list;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        bestParamRepository.findAll(Example.of(BestParam.builder().state(0).build()))
                .map(e -> {
                    e.setState(-1);
                    return e;
                })
                .flatMap(e -> bestParamRepository.save(e))
                .subscribe();
    }
}
