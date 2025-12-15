package com.migrator.core.impl.postgres;

import com.migrator.core.DbVersionRepository;
import com.migrator.model.MigrationScript;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class PostgresDbVersionRepository implements DbVersionRepository {
    private final Connection connection;

    /**
     * @param connection JDBC connection used for queries
     */
    public PostgresDbVersionRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Fetches all migration versions that are already applied.
     *
     * @return A Set of version strings, e.g., ["202501100930", "202501120101"]
     */
    public Set<String> getAppliedVersions() {
        Set<String> versions = new HashSet<>();

        try {
            ensureSchemaMigrationsTable();

            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT version FROM schema_migrations"
            )) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    versions.add(rs.getString("version"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch applied migrations", e);
        }

        return versions;
    }

    /**
     * Saves a successfully executed migration to the database.
     *
     * @param script MigrationScript that was just executed
     */
    public void save(MigrationScript script) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO schema_migrations (version, description, checksum) VALUES (?, ?, ?)"
        )) {
            stmt.setString(1, script.getVersion());
            stmt.setString(2, script.getDescription());
            stmt.setString(3, script.getChecksum());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save migration record for version " + script.getVersion(), e
            );
        }
    }

    private void ensureSchemaMigrationsTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS schema_migrations (
                id SERIAL PRIMARY KEY,
                version VARCHAR(50) NOT NULL UNIQUE,
                description VARCHAR(255),
                applied_at TIMESTAMP NOT NULL DEFAULT NOW(),
                checksum VARCHAR(255)
            )
        """);
        }
    }
}
