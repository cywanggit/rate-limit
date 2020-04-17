package com.example.demo.base;

import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import javax.annotation.Resource;

/**
 * @name：
 * @author：xiong.qiu
 * @date： 2019/11/20 9:47
 */
public abstract class AbstractDao<M> implements Dao<M>, InitializingBean {
    protected MongoTemplate mongoTemplate;
    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private MongoMappingContext mongoMappingContext;

    @Autowired
    private DefaultMongoTypeMapper mongoTypeMapper;

    /**
     * mongodb Factory
     */
    protected MongoDbFactory mongoDbFactory;

    protected MongoConverter mongoConverter;

    @Override
    public void afterPropertiesSet() throws Exception {
        mongoDbFactory = new SimpleMongoDbFactory(mongoClient, getDbName());

        mongoConverter = new MappingMongoConverter(mongoDbFactory, mongoMappingContext);
        ((MappingMongoConverter) mongoConverter).setTypeMapper(mongoTypeMapper);

        mongoTemplate = new MongoTemplate(mongoDbFactory, mongoConverter);
        mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);

    }

    /**
     * 获取数据库名称
     *
     * @return
     */
    protected abstract String getDbName();

    /**
     * 获取集合名称
     *
     * @return
     */
    protected abstract String getCollection();
}
