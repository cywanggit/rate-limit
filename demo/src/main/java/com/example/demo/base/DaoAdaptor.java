package com.example.demo.base;

import com.mongodb.client.model.IndexOptions;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;

/**
 * @name：
 * @author：xiong.qiu
 * @date： 2019/11/20 9:51
 */
public abstract class DaoAdaptor<M> extends AbstractDao<M> {
    @Value("${mongo.dbname}")
    protected String dbName;

    @Override
    public boolean insert(M m) {
        return false;
    }

    @Override
    public boolean insert(String collection, M m) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(M m) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(String collection, M m) {
        return false;
    }

    @Override
    public void createIndex(String collection, Bson dbObject, IndexOptions indexOptions) {
        mongoTemplate.getCollection(collection).createIndex(dbObject, indexOptions);
    }
}
