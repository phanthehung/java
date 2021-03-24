package com.example.purchase.boundedcontext.shared.cache;


import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisClientTest {

    @InjectMocks
    private RedisClient cache;

    @Mock
    private JedisPool jedisPool;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPutThenGetString() {
        Gson gson = new Gson();
        Jedis jedis = Mockito.mock(Jedis.class);
        String key = "key";
        String value = "value";
        Mockito.when(jedisPool.getResource()).thenReturn(jedis);
        Mockito.when(jedis.set(Mockito.anyString(), Mockito.any())).thenReturn("ok");
        Mockito.when(jedis.expire(key, 1000)).thenReturn(1000L);

        Mockito.when(jedis.get(Mockito.anyString())).thenReturn(gson.toJson(value));
        Mockito.when(jedis.exists(key)).thenReturn(true);

        cache.put(key, value, 1000);
        String cachedValue = (String) cache.get(key, value.getClass());
        Assertions.assertEquals(value, cachedValue);
    }

    @Test
    public void testPutThenGetBoolean() {
        Gson gson = new Gson();
        Jedis jedis = Mockito.mock(Jedis.class);
        String key = "key";
        boolean value = true;
        Mockito.when(jedisPool.getResource()).thenReturn(jedis);
        Mockito.when(jedis.set(Mockito.anyString(), Mockito.any())).thenReturn("ok");
        Mockito.when(jedis.expire(key, 1000)).thenReturn(1000L);

        Mockito.when(jedis.get(Mockito.anyString())).thenReturn(gson.toJson(value));
        Mockito.when(jedis.exists(key)).thenReturn(true);

        cache.put(key, value, 1000);
        boolean cachedValue = (boolean) cache.get(key, Boolean.class);
        Assertions.assertEquals(value, cachedValue);
    }

    @Test
    public void testGetNotExist() {
        String key = "key";
        String value = "value";
        Jedis jedis = Mockito.mock(Jedis.class);
        Mockito.when(jedisPool.getResource()).thenReturn(jedis);
        Mockito.when(jedis.exists(key)).thenReturn(false);
        Mockito.when(jedis.get(key)).thenReturn(value);

        Assertions.assertEquals(null, cache.get(key, String.class));
    }

    @Test
    public void testRemove() {
        String key = "key";
        Jedis jedis = Mockito.mock(Jedis.class);
        Mockito.when(jedisPool.getResource()).thenReturn(jedis);
        Mockito.when(jedis.del(key)).thenReturn(1L);

        cache.remove(key);
        Mockito.verify(jedis, Mockito.times(1)).del(key);
    }

    @Test
    public void exist() {
        String key = "key";
        Jedis jedis = Mockito.mock(Jedis.class);
        Mockito.when(jedisPool.getResource()).thenReturn(jedis);
        Mockito.when(jedis.exists(key)).thenReturn(true);

        Assertions.assertTrue(cache.exist(key));
        Mockito.verify(jedis, Mockito.times(1)).exists(key);
    }
}
