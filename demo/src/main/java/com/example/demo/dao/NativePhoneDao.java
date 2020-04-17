package com.example.demo.dao;

import com.alibaba.fastjson.JSON;
import com.example.demo.base.DaoAdaptor;
import com.example.demo.entity.PhoneEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author cw
 * @Copyright (C),  2020-2020, 创蓝253
 * @Description
 * @FileName PhoneDao
 * @create 2020-04-11 15:21
 */
@Component
public class NativePhoneDao extends DaoAdaptor<PhoneEntity> {

    private String collection;

    @Override
    protected String getDbName() {
        return dbName;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    @Override
    protected String getCollection() {
        return "native_mobile";
    }



    public long count(Query query){
        return mongoTemplate.count(query,getCollection());
    }

    @Override
    public List<PhoneEntity> queryList(long start, int limit) {
        Query query = new Query();
        query.skip(start).limit(limit);
        return mongoTemplate.find(query,PhoneEntity.class,getCollection());
    }

    @Override
    public List<PhoneEntity> queryListByAgg(long start, long limit) {
        AggregationOperation skip = new SkipOperation(start);
        AggregationOperation aggregationOperation = new LimitOperation(limit);
        Aggregation aggregation = Aggregation.newAggregation(skip,aggregationOperation);
        AggregationResults<PhoneEntity> results = mongoTemplate.aggregate(aggregation, getCollection(), PhoneEntity.class);
        return results.getMappedResults();
    }


}
