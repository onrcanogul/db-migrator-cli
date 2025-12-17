# Using db-migrator in CI/CD Pipelines

This document explains how to safely run **db-migrator** in CI/CD environments.

The tool is designed to be:
- non-interactive
- idempotent
- safe for parallel CI executions
- compatible with containerized and ephemeral runners

---

## Core Principle

**Database migrations must run exactly once per database.**

In CI/CD:
- multiple jobs may start at the same time
- multiple services may deploy concurrently
- multiple environments may reuse the same database

To solve this, db-migrator uses **database-level locking**.

Only **one instance** can apply migrations at a time.

---

## Recommended CI/CD Flow

### 1. Build the migration artifact

```bash
mvn clean package
```

This produces a fat (shaded) JAR:
```bash
db-migrator-<version>-shaded.jar
```

This JAR contains:
- application code
- JDBC drivers
- all runtime dependencies


### 2. Store migrations in versioned SQL files

Example Directory:
```bash
migrations/
 ├── 20251215201900_create_users.sql
 ├── 20251215203000_add_email_column.sql
```
Rules:

- filenames must be ordered
- versions must be unique
- scripts must be idempotent where possible


### 3. Run migrations in CI/CD

Example (PostgreSQL):

```bash
java -jar db-migrator-1.0.0-shaded.jar \
  --db.type=postgres \
  --db.host=$DB_HOST \
  --db.port=5432 \
  --db.name=$DB_NAME \
  --db.user=$DB_USER \
  --db.pass=$DB_PASS \
  --migrations=./migrations
```

Example (MSSQL):
```bash
java -jar db-migrator-1.0.0-shaded.jar \
  --db.type=mssql \
  --db.host=$DB_HOST \
  --db.port=1433 \
  --db.name=$DB_NAME \
  --db.user=sa \
  --db.pass=$DB_PASS \
  --migrations=./migrations
```
---
## Locking Behavior in CI/CD

When a migration starts:
1. A database-level lock is acquired
2. Other migration attempts will block or fail
3. Scripts are executed sequentially
4. Successful migrations are recorded
5. The lock is released

This guarantees:
- no duplicate executions
- no race conditions
- no partial schema state

Thread-level or JVM-level locking is intentionally NOT used.


### Multiple Services, One Database
If multiple services share the same database:

Safe:

- all services run db-migrator
- only one applies migrations
- others skip already applied versions

Not safe:
- custom migration logic per service
- manual SQL execution in CI

### Failure Handling

Each migration is retried up to a configurable limit

If a migration fails:
- the transaction is rolled back
- the migration is NOT marked as applied
- CI job fails explicitly

This ensures:
- no silent failures
- no corrupted migration state

---
## Best Practices

- Run migrations before application startup
- Use one migration directory per database
- Keep migrations small and incremental
- Never modify an already applied migration
- Always create a new version instead




