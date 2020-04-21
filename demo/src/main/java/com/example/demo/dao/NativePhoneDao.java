package com.example.demo.dao;

import com.alibaba.fastjson.JSON;
import com.example.demo.base.DaoAdaptor;
import com.example.demo.entity.PhoneEntity;
import com.example.demo.shard.NativePhoneShard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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


    @Qualifier("native.phone.shard")
    @Autowired
    private NativePhoneShard nativePhoneShard;

    @Override
    protected String getDbName() {
        return dbName;
    }




    public long count(Query query,String collection){
        return mongoTemplate.count(query,collection);
    }

    @Override
    public List<PhoneEntity> queryList(long start, int limit,String collection) {
        Query query = new Query();
        query.skip(start).limit(limit);
        return mongoTemplate.find(query,PhoneEntity.class,collection);
    }

    @Override
    public List<PhoneEntity> queryListByAgg(long start, long limit,String collection) {
        AggregationOperation skip = new SkipOperation(start);
        AggregationOperation aggregationOperation = new LimitOperation(limit);
        Aggregation aggregation = Aggregation.newAggregation(skip,aggregationOperation);
        AggregationResults<PhoneEntity> results = mongoTemplate.aggregate(aggregation, collection, PhoneEntity.class);
        return results.getMappedResults();
    }




    @Override
    public boolean insertBatch(String collection, List<PhoneEntity> phoneEntities){
        mongoTemplate.insert(phoneEntities,collection);
        return true;
    }

    @Override
    public boolean insert(String collection, PhoneEntity phoneEntity) {
        mongoTemplate.insert(phoneEntity,collection);
        return true;
    }
}
