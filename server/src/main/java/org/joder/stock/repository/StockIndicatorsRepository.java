package org.joder.stock.repository;

import org.bson.types.ObjectId;
import org.joder.stock.model.entity.StockIndicator;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Joder 2020/11/7 17:19
 */
@Repository
public interface StockIndicatorsRepository extends ReactiveMongoRepository<StockIndicator, ObjectId> {

    Mono<Void> deleteAllByTsCode(String tsCode);

    Flux<StockIndicator> getAllByTsCodeAndDateBetween(String tsCode, String startDate, String endDate);
}
