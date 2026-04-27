package com.flance.framework.common.datasource.config;

public class SchemaCache {

    private static final java.util.Map<String, Class<?>> TABLE_TO_ENTITY = new java.util.HashMap<>();

    public static void register(String tableName, Class<?> entityClass) {
        TABLE_TO_ENTITY.put(tableName, entityClass);
    }



}
