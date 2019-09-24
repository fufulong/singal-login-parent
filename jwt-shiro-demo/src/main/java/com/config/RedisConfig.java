package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.Charset;

/**
 * redis的配置类
 * @author fufulong
 * @since 2019-09-19
 */
@Configuration
public class RedisConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisTemplate<String,Object> redisTemplate(){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<String,Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
     //设置 redis 的key 的序列化策略是直接把数据的字节按照指定的编码来变成字符串
        redisTemplate.setKeySerializer(new StringRedisSerializer(Charset.forName("UTF-8")));
        // 这个序列化器 会把 对象的每个属性的名称和值都变成 redis 的key-value,一般不这样
      /*     Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        //前面这个参数是设置所有 get,set,field域都要序列化和反序列化,
        // 后面这个参数是表示Object的所有private,public,default.protected的属性都要经过序列化和反序列化
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //设置Object是Date类型的时候,序列化的格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        jsonRedisSerializer.setObjectMapper(objectMapper);*/

        //设置redis的value 的序列化侧策略
//        redisTemplate.setValueSerializer(new StringRedisSerializer(Charset.forName("UTF-8")));
        redisTemplate.setValueSerializer(new StringRedisSerializer(Charset.forName("UTF-8")));
        //设置 redis 的值是 map 的时候,map的key的序列化策略是字节直接变成个字符串
        redisTemplate.setHashKeySerializer(new StringRedisSerializer(Charset.forName("UTF-8")));
        redisTemplate.setHashValueSerializer(new StringRedisSerializer(Charset.forName("UTF-8")));
        return  redisTemplate;
    }

    @Bean
    public ValueOperations<String,Object> valueOperations(RedisTemplate<String,Object> redisTemplate){
        return redisTemplate.opsForValue();
    }

    @Bean
    public HashOperations<String,String,Object> hashOperations(RedisTemplate redisTemplate){
        return redisTemplate.opsForHash();
    }

    @Bean
    public ListOperations<String,Object> listOperations(RedisTemplate redisTemplate){
        return redisTemplate.opsForList();
    }

    @Bean
    public SetOperations<String,Object> setOperations(RedisTemplate redisTemplate){
        return redisTemplate.opsForSet();
    }

    @Bean
    public ZSetOperations<String,Object> zSetOperations(RedisTemplate redisTemplate){
        return redisTemplate.opsForZSet();
    }


}
