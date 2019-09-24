package com.common;

import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis 操作的工具类
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private ValueOperations<String,Object> valueOperations;
    @Autowired
    private HashOperations<String,String,Object> hashOperations;
    @Autowired
    private ListOperations<String,Object>  listOperations;
    @Autowired
    private SetOperations<String,Object> setOperations;
    @Autowired
    private ZSetOperations<String,Object> zSetOperations;
    /**
     * 默认的 redis 的key的过期时间 ,单位 秒,默认是 1天
     */
    private static final Long DEFAULT_EXTIRE_TIME = 24 * 60 * 60L;
    /**
     * key永不过期,过期时间就是 -1
     */
    private static final Long NEVER_EXPIRE = -1L;

    /**
     * 判断 指定的 key 是否存在
     * @param key : redis 的key
     * @return true:存在,false:不存在
     */
    public Boolean hasKey(String key){
        Boolean result = redisTemplate.hasKey(key);
        return result;
    }
    /**
     * 设置key的过期时间
     * @param key:指定的key
     */
    public void expireKeyDefult(String key){
        redisTemplate.expire(key,DEFAULT_EXTIRE_TIME, TimeUnit.SECONDS);
    }

    /**
     * 设置 指定key的过期时间
     * @param key:指定的key
     * @param time:过期时间长度
     * @param timeUnit:时间单位
     */
    public void expireKey(String key ,Long time,TimeUnit timeUnit){
        redisTemplate.expire(key,time,timeUnit);
    }

    /**
     * 指定特定的key在指定的将来某个时间过期
     * @param key: Key
     * @param futrue:将来的过期时间
     */
    public void expireKeyAtDate(String key ,Date futrue){
        long now = System.currentTimeMillis();
        long futrueTime = futrue.getTime();
        if(futrueTime - now <= 0){
            throw new RuntimeException("过期时间必须是未来时间");
        }
        redisTemplate.expireAt(key,futrue);

    }

    /**
     * 增加 value 是 pojo的 key 和 value,并指定过期时间
     */
    public void addStringValue(String key ,Object object,Long time ,TimeUnit unit){
        valueOperations.set(key,object,time,unit);
    }


    /**
     * 删除key
     * @param key:要删除的key
     */
    public void deleteKey(String key){
        redisTemplate.delete(key);
    }

    /**
     * 向 redis 中新增一个 指定 的key,value 是指定的map
     * @param key
     * @param map
     */
    public void addHashvalue(String key, Map<String,Object> map){
        hashOperations.putAll(key,map);
    }

    /**
     * 在已经存在的 value 是 hash 中新增一个 键值对
     * @param key: redis 的key
     * @param hashKey: 要新增的hash的key
     * @param value: 要新增的 hash 的一个 value
     */
    public void addOneHashEntry(String key,String hashKey,Object value){
        hashOperations.put(key,hashKey,value);
    }

    /**
     * 删除已经存在的 key中 的一个 Entry
     * @param key:redis 的key
     * @param hashKey : hash的key
     */
    public void deleteOneHashEntry(String key,String hashKey){
        if (redisTemplate.hasKey(key)){
            throw new RuntimeException("redis 中没有 key = " + key + "键");
        }

        hashOperations.delete(key,hashKey);
    }




}
