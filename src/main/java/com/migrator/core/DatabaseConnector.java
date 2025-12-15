package com.migrator.core;

import com.migrator.model.DbConfig;

import java.sql.Connection;

public interface DatabaseConnector {
    Connection connect(DbConfig config) throws Exception;
}
