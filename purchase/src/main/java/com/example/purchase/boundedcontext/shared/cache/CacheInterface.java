package com.example.purchase.boundedcontext.shared.cache;

public interface CacheInterface<T> {
    void put(String key, T object, int timeToLive);
    T get(String key, Class<T> classOfT);
    void remove(String key);
    boolean exist(String key);
}
