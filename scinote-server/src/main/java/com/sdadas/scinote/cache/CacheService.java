package com.sdadas.scinote.cache;

import com.sdadas.scinote.cache.model.Cached;

import java.util.List;

/**
 * @author Sławomir Dadas
 */
public interface CacheService {

    <T> void initCache(Class<T> type);

    <T> Cached<T> get(String key, Class<T> type);

    <T> List<Cached<T>> get(List<String> keys, Class<T> type);

    <T> List<Cached<T>> all(boolean loadObjects, Class<T> type);

    <T> void put(String key, T object, Class<? super T> type);
}
