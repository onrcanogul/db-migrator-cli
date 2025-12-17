package com.migrator.factory;

import com.migrator.core.impl.MigrationLock;
import com.migrator.core.impl.mssql.MssqlMigrationLock;
import com.migrator.core.impl.oracle.OracleMigrationLock;
import com.migrator.core.impl.postgres.PostgresMigrationLock;

import java.sql.Connection;

public class MigrationLockFactory {

    public static MigrationLock create(String dbType, Connection connection) {
        return switch (dbType.toLowerCase()) {
            case "postgres" -> new PostgresMigrationLock(connection);
            case "mssql" -> new MssqlMigrationLock(connection);
            case "oracle" -> new OracleMigrationLock(connection);
            default -> throw new IllegalArgumentException(
                    "Unsupported database type: " + dbType
            );
        };
    }
}

