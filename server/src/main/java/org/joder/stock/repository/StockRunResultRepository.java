package org.joder.stock.repository;

import org.bson.types.ObjectId;
import org.joder.stock.model.entity.StockRunResult;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Joder 2020/8/22 17:37
 */
public interface StockRunResultRepository extends ReactiveMongoRepository<StockRunResult, ObjectId> {

    @Query("{ 'strategy_uuid' : '?0', 'ts_code': '?1' }")
    Flux<StockRunResult> getAllByStrategy(String uuid, String tsCode);

    @Query("{ 'strategy_uuid' : '?0' }")
    Flux<StockRunResult> getAllByStrategyByUUid(String uuid);

    Mono<Void> deleteAllByStrategyUuid(String uuid);
}
