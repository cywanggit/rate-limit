package com.chuanglan.config;

import com.chuanglan.entity.Rate;
import com.chuanglan.entity.RedisRate;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * @copyright (C),  2019-2019, 创蓝253
 * @Description Redis
 * @FileName RedisRateLimiter
 * @auther cw
 * @create 2019-11-07 11:06
 */
public class RedisLuaRateLimiter extends AbstractRateLimiter{

    private RedisRate jedis;

    List<String> keys = new ArrayList<>();

    public RedisLuaRateLimiter(Rate rate) {
        this.rate = rate;
        if (this.rate instanceof RedisRate){
            jedis = (RedisRate) this.rate;
        }
        keys.add(jedis.getLimitTimeMillisKey());
        keys.add(jedis.getTimeKey());
    }



    @Override
    protected void setRate(Rate rate) {


    }

    private Jedis getClient(){
        return jedis.getClient();
    }

    String luaScirpt ="\n" +
            "local lock = redis.call('setnx','rate_lock',1)\n" +
            "local limitTimeMillis = redis.call('get',KEYS[1])\n" +
            "redis.call('expire','rate_lock',limitTimeMillis/1000)\n" +
            "\n" +
            "if  1 ~= lock then\n" +
            "\treturn 0\n" +
            "end\n" +
            "\n" +
            "local current = tonumber(ARGV[1])\n" +
            "local start = redis.call('get','default_startLimitTimeMillis')\n" +
            "local a = current - start\n" +
            "if  a >= tonumber(limitTimeMillis) then\n" +
            "\tredis.call('set','default_startLimitTimeMillis',current)\n" +
            "\tredis.call('set','default_currentTime',1)\n" +
            "\tredis.call('del','rate_lock')\n" +
            "\treturn 1\n" +
            "end\n" +
            "\n" +
            "local currentTime = redis.call('get','default_currentTime')\n" +
            "print(currentTime)\n" +
            "local time = redis.call('get',KEYS[2])\n" +
            "if tonumber(currentTime) >= tonumber(time) then\n" +
            "    redis.call('del','rate_lock')\n" +
            "\treturn 2\n" +
            "end\n" +
            "redis.call('incr','default_currentTime')\n" +
            "redis.call('del','rate_lock')\n" +
            "return 1\n" +
            "\n" +
            "\n";


    @Override
    public boolean limit() {
        Jedis client = getClient();
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(System.currentTimeMillis()));
        Object eval = null;
        try{
            eval = client.eval(luaScirpt, keys, values);//使用lua脚本 可以减少网络开销  大大提升了性能和安全 从之前的分部操作(需要分布式锁) 提升为 原子操作
            if ("1".equals(eval.toString())){
                return true;
            }else if ("0".equals(eval.toString())){  //分开 0 和 2 后续可以进行统计操作失败的原因
                return false;
            }else if ("2".equals(eval.toString())){
                return false;
            }
        }catch(Exception e){

        }finally{
            client.close();
        }
        throw new RuntimeException("lua脚本数据返回异常:");

    }


}
