package com.example.demo.base;

/**
 * 分表策略
 */
public interface ShardStrategy<M> {

    /**
     * 分表策略实现
     * @param m
     * @return 返回表后缀
     */
    String doSharding(M m);

}
