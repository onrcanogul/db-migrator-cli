package com.migrator.factory;

import com.migrator.core.DatabaseConnector;
import com.migrator.core.impl.mssql.MssqlConnector;
import com.migrator.core.impl.oracle.OracleConnector;
import com.migrator.core.impl.postgres.PostgresConnector;
import com.migrator.model.DatabaseType;

public class DatabaseConnectorFactory {

    public static DatabaseConnector create(DatabaseType type) {
        return switch (type) {
            case POSTGRES -> new PostgresConnector();
            case MSSQL   -> new MssqlConnector();
            case ORACLE  -> new OracleConnector();
        };
    }
}
