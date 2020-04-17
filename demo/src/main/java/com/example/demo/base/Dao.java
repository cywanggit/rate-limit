package com.example.demo.base;

import com.mongodb.client.model.IndexOptions;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @name：
 * @author：xiong.qiu
 * @date： 2019/11/20 9:47
 */
public interface Dao<M> {
    /**
     * 新增
     * @param m
     * @return true 成功， false 失败
     */
    boolean insert(M m);

    /**
     * 新增数据
     * @param collection 指定集合名称
     * @param m
     * @return
     */
    boolean insert(String collection, M m);

    /**
     * 保存或者更新
     * @param m
     * @return true 成功， false 失败
     */
    boolean saveOrUpdate(M m);

    /**
     * 保存活更新数据
     * @param collection 集合名称
     * @param m
     * @return
     */
    boolean saveOrUpdate(String collection, M m);

    /**
     * 分页查询
     * @param start
     * @param limit
     * @return
     */
    List<M> queryList(long start, int limit);


    /**
     * 使用聚合方式分页查询
     * @param start
     * @param limit
     * @return
     */
    List<M> queryListByAgg( long start, long limit);

    /**
     * 创建索引
     * collection 集合名称
     * dbObject  索引字段
     * indexOptions 索引设置
     */
    void createIndex(String collection, Bson dbObject, IndexOptions indexOptions);
}
