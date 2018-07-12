package com.rd.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by XingYun on 2017/5/4.
 */
public class SimpleLRUCache<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 1L;
    protected int maxElements;

    public SimpleLRUCache(int maxSize) {
        super(maxSize, 0.75F, true);
        this.maxElements = maxSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this.maxElements;
    }

//    protected void afterNodeRemoval(Map.Entry<K,V> e) { //
//        super.afterNodeRemoval(e);
//    }

}
