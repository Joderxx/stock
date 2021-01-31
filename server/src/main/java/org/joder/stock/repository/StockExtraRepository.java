package org.joder.stock.repository;

import org.bson.types.ObjectId;
import org.joder.stock.model.entity.StockExtra;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Joder 2020/12/6 15:14
 */
@Repository
public interface StockExtraRepository extends ReactiveMongoRepository<StockExtra, ObjectId> {

    Flux<StockExtra> findAllByAttention(Boolean attention);

    Flux<StockExtra> findAllByTsCode(String tsCode);

    Mono<StockExtra> findByTsCode(String tsCode);

    Mono<Void> deleteByTsCode(String tsCode);
}
