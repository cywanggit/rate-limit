package com.example.demo.base;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractShardStrategy<M> implements ShardStrategy<M> {


    @Override
    public String doSharding(M m) {
        String collection = generateColectionName();

        String suffix = generateColectionSuffix(m);
        if (StringUtils.isBlank(suffix)) {
            return collection;
        }
        return collection + "_" + suffix;
    }

    /**
     * 生成表后缀
     *
     * @return
     */
    protected String generateColectionSuffix(M m) {
        return null;
    }


    protected abstract String generateColectionName();
}
