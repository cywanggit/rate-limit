package com.example.demo.shard;

import com.example.demo.base.AbstractShardStrategy;
import org.springframework.stereotype.Component;

/**
 * @author cw
 * @Copyright (C),  2020-2020, 创蓝253
 * @Description
 * @FileName NativePhoneShard
 * @create 2020-04-17 13:59
 */
@Component("native.phone.shard")
public class NativePhoneShard extends AbstractShardStrategy<String> {
    @Override
    protected String generateColectionName() {
        return "native_mobile";
    }

    @Override
    protected String generateColectionSuffix(String s) {
        String substring = s.substring(8);
        // 分表规则
        return substring;
    }
}
