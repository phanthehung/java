package com.example.purchase.boundedcontext.shared.cache;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class RedisClient<T> implements CacheInterface<T> {

    private Gson gson;
    private JedisPool jedisPool;

    public RedisClient(Gson gson, JedisPool jedisPool) {
        this.gson = gson;
        this.jedisPool = jedisPool;
    }

    @Override
    public void put(String key, T object, int timeToLive) {
        String value = gson.toJson(object);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
            jedis.expire(key, timeToLive);
        }
    }

    @Override
    public T get(String key, Class<T> classOfT) {
        try (Jedis jedis = jedisPool.getResource()) {
            if(jedis.exists(key)) {
                String value = jedis.get(key);
                return gson.fromJson(value, classOfT);
            }
            return null;
        }
    }

    @Override
    public void remove(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    @Override
    public boolean exist(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        }
    }
}
