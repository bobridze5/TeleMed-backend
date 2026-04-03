package com.bobridze5.TeleMed_backend.core.service.auth;

public interface RedisStoreService<K, V> {
    void save(K key, V value, long time);

    V get(K key);

    void delete(K key);
}
