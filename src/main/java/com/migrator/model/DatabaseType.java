package com.migrator.model;

public enum DatabaseType {
    POSTGRES,
    MSSQL,
    ORACLE;

    public static DatabaseType from(String value) {
        return DatabaseType.valueOf(value.toUpperCase());
    }
}
