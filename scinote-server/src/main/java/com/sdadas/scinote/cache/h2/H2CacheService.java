package com.sdadas.scinote.cache.h2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.sdadas.scinote.cache.CacheService;
import com.sdadas.scinote.cache.model.Cached;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
@Service
public class H2CacheService implements CacheService {

    private static final Logger LOG = LoggerFactory.getLogger(H2CacheService.class);

    private final Map<Class<?>, H2Cache<?>> caches = new HashMap<>();

    private final NamedParameterJdbcTemplate template;

    private final PlatformTransactionManager manager;

    private final ObjectMapper mapper;

    @Autowired
    public H2CacheService(DataSource dataSource, ObjectMapper mapper) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.manager = new DataSourceTransactionManager(dataSource);
        this.mapper = mapper;
    }

    @Override
    public <T> void initCache(Class<T> type) {
        H2Cache<T> cache = new H2Cache<>(type);
        template.getJdbcOperations().execute(cache.create());
        caches.put(type, cache);
    }

    @Override
    public <T> Cached<T> get(String key, Class<T> type) {
        H2Cache<T> cache = cache(type);
        Map<String, Object> params = Collections.singletonMap("id", key);
        List<CachedValue> values = template.query(cache.selectOne(), params, cache.rowMapper);
        List<Cached<T>> results = convert(values, type);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public <T> List<Cached<T>> get(List<String> keys, Class<T> type) {
        H2Cache<T> cache = cache(type);
        Map<String, Object> params = Collections.singletonMap("ids", keys);
        List<CachedValue> values = template.query(cache.selectMany(), params, cache.rowMapper);
        return convert(values, type);
    }

    @Override
    public <T> List<Cached<T>> all(boolean loadObjects, Class<T> type) {
        H2Cache<T> cache = cache(type);
        List<CachedValue> values = template.query(cache.selectAll(loadObjects), cache.rowMapper);
        return convert(values, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void put(String key, T object, Class<? super T> type) {
        transaction(() -> {
            H2Cache<T> cache = (H2Cache<T>) cache(type);
            Map<String, Object> params = Collections.singletonMap("id", key);
            template.update(cache.deleteOne(), params);
            CachedValue value = new CachedValue(mapper, key, object);
            SimpleJdbcInsert insert = new SimpleJdbcInsert(template.getJdbcTemplate()).withTableName(cache.table);
            insert.execute(new BeanPropertySqlParameterSource(value));
        });
    }

    @SuppressWarnings("unchecked")
    private <T> H2Cache<T> cache(Class<T> clazz) {
        H2Cache<T> cache = (H2Cache<T>) caches.get(clazz);
        if(cache == null) {
            throw new NullPointerException("Cache not found for class " + clazz.getSimpleName());
        }
        return cache;
    }

    private <T> List<Cached<T>> convert(List<CachedValue> results, Class<T> type) {
        return results.stream().map(val -> val.convert(mapper, type)).collect(Collectors.toList());
    }

    private void transaction(Runnable runnable) {
        TransactionTemplate tt = new TransactionTemplate(manager);
        tt.executeWithoutResult((status) -> {
            try {
                runnable.run();
            } catch (Exception ex) {
                LOG.error("Error executing transaction", ex);
                status.setRollbackOnly();
            }
        });
    }

    private static class H2Cache<T> {
        private String table;
        private RowMapper<CachedValue> rowMapper;

        public H2Cache(Class<T> type) {
            String className = type.getSimpleName();
            this.table = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.UPPER_UNDERSCORE).convert(className);
            this.rowMapper = new BeanPropertyRowMapper<>(CachedValue.class);
        }

        public String create() {
            List<String> columns = new ArrayList<>();
            columns.add("key varchar(1000) primary key");
            columns.add("name varchar(10000)");
            columns.add("updated timestamp not null");
            columns.add("value clob not null");
            String sql = "create table if not exists %s (%s)";
            return String.format(sql, table, String.join(", ", columns));
        }

        public String selectOne() {
            return String.format("select * from %s where key = :id", table);
        }

        public String selectMany() {
            return String.format("select * from %s where key in (:ids)", table);
        }

        public String deleteOne() {
            return String.format("delete from %s where key = :id", table);
        }

        public String selectAll(boolean loadObjects) {
            if(loadObjects) {
                return String.format("select * from %s", table);
            } else {
                return String.format("select key, name, updated from %s", table);
            }
        }
    }
}
