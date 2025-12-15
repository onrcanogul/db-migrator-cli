package com.migrator.core.impl.postgres;

import com.migrator.core.DatabaseConnector;
import com.migrator.model.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;

public class PostgresConnector implements DatabaseConnector {
    @Override
    public Connection connect(DbConfig config) throws Exception {
        String url = "jdbc:postgresql://%s:%d/%s"
                .formatted(
                        config.host(),
                        config.port(),
                        config.database()
                );
        return DriverManager.getConnection(
                url,
                config.user(),
                config.password()
        );
    }
}
