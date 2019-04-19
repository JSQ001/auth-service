package com.hand.hcf.core.util;

import java.util.Map;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/5/9 14:15
 * @remark 自定义键值对
 */
public class CustomEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public CustomEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return value;
    }

}
