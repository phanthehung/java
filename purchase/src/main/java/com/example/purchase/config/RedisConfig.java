package com.example.purchase.config;

import com.example.purchase.boundedcontext.shared.cache.CacheInterface;
import com.example.purchase.boundedcontext.shared.cache.RedisClient;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Value("${cache.password}")
    private String password;

    @Value("${cache.host}")
    private String host;

    @Value("${cache.port}")
    private int port;

    @Value("${cache.timeout}")
    private int timeout;

    @Value("${cache.idle}")
    private int idle;

    @Value("${cache.total}")
    private int total;

    @Bean
    public JedisPool getJedisPool() {
        if (System.getenv("CACHE_PASSWORD") != null && !System.getenv("CACHE_PASSWORD").equalsIgnoreCase("")) {
            password = System.getenv("CACHE_PASSWORD");
        }

        if (System.getenv("CACHE_HOST") != null && !System.getenv("CACHE_HOST").equalsIgnoreCase("")) {
            host = System.getenv("CACHE_HOST");
        }

        if (System.getenv("CACHE_PORT") != null && !System.getenv("CACHE_PORT").equalsIgnoreCase("")) {
            port = Integer.parseInt(System.getenv("CACHE_PORT"));
        }

        if (System.getenv("CACHE_TIMEOUT") != null && !System.getenv("CACHE_TIMEOUT").equalsIgnoreCase("")) {
            timeout = Integer.parseInt(System.getenv("CACHE_TIMEOUT"));
        }


        return new JedisPool(getJedisPoolConfig(), host, port, timeout, this.password);
    }

    @Bean
    public CacheInterface redisCache() {
        return new RedisClient(new Gson(), getJedisPool());
    }

    private JedisPoolConfig getJedisPoolConfig() {
        if (System.getenv("CACHE_IDLE") != null && !System.getenv("CACHE_IDLE").equalsIgnoreCase("")) {
            idle = Integer.parseInt(System.getenv("CACHE_IDLE"));
        }

        if (System.getenv("CACHE_TOTAL") != null && !System.getenv("CACHE_TOTAL").equalsIgnoreCase("")) {
            total = Integer.parseInt(System.getenv("CACHE_TOTAL"));
        }

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(idle);
        config.setMaxTotal(total);
        return config;
    }
}
