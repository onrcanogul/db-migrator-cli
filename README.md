# Java DB Migrator

Java DB Migrator is a lightweight, framework-free **Java CLI tool** for
managing database schema migrations in a **safe, predictable,
CI/CD-friendly** way.

It is designed as a **deployment-time tool**, not a runtime dependency.\
The migrator runs **outside your application**, prepares the database
schema, and exits.

------------------------------------------------------------------------

## Key Features

-   Versioned SQL migrations using timestamp-based filenames
-   Deterministic execution order
-   Migration history tracking via `schema_migrations`
-   Checksum validation to detect modified scripts
-   Safe to run multiple times (idempotent)
-   No Spring Boot or heavy frameworks
-   Designed for CI/CD pipelines
-   PostgreSQL support via JDBC

------------------------------------------------------------------------

## Design Philosophy

-   Database migrations are a **deployment concern**
-   Applications should **never** manage schema changes at startup
-   Explicit execution is safer than implicit runtime behavior
-   Minimal dependencies lead to predictable production behavior

This approach aligns with enterprise-grade deployment practices.

------------------------------------------------------------------------

## Migration File Structure

All migration scripts must be placed in a `migrations/` directory.

### Naming Convention

    YYYYMMDDHHMM__description.sql

### Example

    202501121030__create_users_table.sql

### Example SQL Content

``` sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
```

Migration files are executed **top to bottom**, ordered by their
version.

------------------------------------------------------------------------

## Migration Tracking Table

``` sql
CREATE TABLE schema_migrations (
    id SERIAL PRIMARY KEY,
    version VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    applied_at TIMESTAMP NOT NULL DEFAULT NOW(),
    checksum VARCHAR(255)
);
```

------------------------------------------------------------------------

## Getting Started

### Build the CLI

``` bash
mvn clean package
```

### Run Migrations

``` bash
java -jar migrator-cli.jar   --db.host=localhost   --db.port=5432   --db.user=postgres   --db.pass=postgres   --db.name=mydb   --migrations=./migrations
```

------------------------------------------------------------------------

## CI/CD Integration

``` yaml
- name: Run DB migrations
  run: |
    java -jar migrator-cli.jar       --db.host=${{ secrets.DB_HOST }}       --db.port=5432       --db.user=${{ secrets.DB_USER }}       --db.pass=${{ secrets.DB_PASS }}       --db.name=${{ secrets.DB_NAME }}       --migrations=./migrations
```

------------------------------------------------------------------------

## Docker Usage

``` bash
docker run   -e DB_HOST=localhost   -e DB_USER=postgres   -e DB_PASS=postgres   -e DB_NAME=mydb   -v $(pwd)/migrations:/migrations   onucinim/java-db-migrator
```

