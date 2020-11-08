package org.joder.stock.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * @author Joder 2020/11/7 17:17
 */
@Data
@Document("stock_indicators")
public class StockIndicator {
    @MongoId(FieldType.OBJECT_ID)
    @JsonIgnore
    private ObjectId id;
    @Field("ts_code")
    private String tsCode;
    private String date;
    private Double pe;
    private Double price;
}
