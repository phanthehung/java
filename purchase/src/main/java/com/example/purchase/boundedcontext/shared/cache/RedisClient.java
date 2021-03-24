package com.example.purchase.boundedcontext.shared.cache;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class RedisClient implements CacheInterface {

    private JedisPool jedisPool;

    public RedisClient(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void put(String key, Object object, int timeToLive) {
        Gson gson = new Gson();
        String value = gson.toJson(object);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
            jedis.expire(key, timeToLive);
        }
    }


    @Override
    public Object get(String key, Class classOfT) {
        Gson gson = new Gson();
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.exists(key)) {
                return gson.fromJson(jedis.get(key), classOfT);
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
