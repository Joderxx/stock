package org.joder.stock.repository;

import org.bson.types.ObjectId;
import org.joder.stock.model.entity.BestParam;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author Joder 2020/8/25 19:53
 */
public interface BestParamRepository extends ReactiveMongoRepository<BestParam, ObjectId> {
}
