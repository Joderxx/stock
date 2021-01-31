package org.joder.stock.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

/**
 * @author Joder 2020/12/6 15:02
 */
@Data
@Document("stock_extra")
@CompoundIndex(name = "code_index", def = "{'ts_code' : 1}")
public class StockExtra {

    @MongoId(FieldType.OBJECT_ID)
    @JsonIgnore
    private ObjectId id;
    @Field("ts_code")
    private String tsCode;
    @Field(value = "stock_type")
    private String stockType;
    private Boolean attention;
}
