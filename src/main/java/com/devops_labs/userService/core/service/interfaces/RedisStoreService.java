package com.devops_labs.userService.core.service.interfaces;

public interface RedisStoreService<K, V> {
    void save(K key, V value, long time);

    V get(K key);

    void delete(K key);
}
