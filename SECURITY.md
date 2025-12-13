# Security Policy

This document describes the security model, responsibilities, and best
practices for using **Java DB Migrator** in production environments.

The goal of this tool is to provide **safe, controlled, and auditable**
database schema migrations without compromising production security.

------------------------------------------------------------------------

## Security Principles

Java DB Migrator is built around the following core security principles:

-   Least privilege access
-   Explicit execution (no implicit runtime behavior)
-   Environment isolation
-   Human error minimization
-   Auditability

------------------------------------------------------------------------

## Database Roles & Permissions

### Do NOT use a superuser

The migrator **must never** be executed using a database superuser or
admin role.

Superuser access dramatically increases the risk of: - Accidental data
loss - Unauthorized schema changes - Irreversible production damage

------------------------------------------------------------------------

### Recommended Setup: Dedicated Migration User

Create a **dedicated database user** specifically for migrations.

Example (PostgreSQL):

``` sql
CREATE USER migrator_user WITH PASSWORD 'strong_password';

GRANT CONNECT ON DATABASE mydb TO migrator_user;
GRANT USAGE ON SCHEMA public TO migrator_user;

GRANT CREATE, ALTER, REFERENCES
ON SCHEMA public
TO migrator_user;
```

Recommended permissions: - CREATE TABLE - ALTER TABLE - CREATE INDEX -
INSERT / SELECT on schema_migrations

Explicitly avoid: - SUPERUSER - DROP DATABASE - GRANT privileges

------------------------------------------------------------------------

## Migration State Storage

Migration state is stored **inside the target database** using the
schema_migrations table.

Each environment maintains its own state:

-   Development → dev database
-   Staging → staging database
-   Production → production database

Migration state **must never** be shared across environments.

------------------------------------------------------------------------

## Credential Management

### Forbidden

-   Hardcoded credentials in source code
-   Credentials committed to version control
-   Plaintext passwords in configuration files

------------------------------------------------------------------------

### Required

All database credentials **must** be provided via:

-   CI/CD secrets
-   Kubernetes Secrets
-   Cloud secret managers (AWS SSM, GCP Secret Manager, Vault, etc.)

Example:

``` text
DB_USER=migrator_user
DB_PASS=********
```

------------------------------------------------------------------------

## CI/CD Execution Model

Java DB Migrator is designed to run:

-   During deployment
-   Before application startup
-   As a one-time execution step

If a migration fails: - The pipeline **must stop** - The application
**must not be deployed**

This prevents partial or inconsistent production states.

------------------------------------------------------------------------

## Change Management & Reviews

Migration scripts are treated as **code**.

Required process: 1. Migration SQL added via Pull Request 2. Code review
required 3. Approved changes merged 4. CI/CD pipeline executes migration

Direct execution of ad-hoc SQL against production databases is strongly
discouraged.

------------------------------------------------------------------------

## Safe Execution Practices

-   Keep migrations small and focused
-   Avoid long-running or blocking operations
-   Prefer additive changes (new tables, new columns)
-   Avoid destructive changes without a rollback plan
-   Never modify an already-applied migration script

Checksum validation exists to detect unauthorized modifications.

------------------------------------------------------------------------

## Logging & Audit

Migration execution should be logged by the CI/CD system, including: -
Migration version - Execution timestamp - Execution status
(success/failure) - Deployment identifier (pipeline run, commit hash)

These logs provide traceability and support incident analysis.

------------------------------------------------------------------------

## Incident Response

If a migration causes unexpected behavior in production:

1.  Stop further deployments immediately
2.  Identify the migration version involved
3.  Apply corrective migration if needed
4.  Document the incident and resolution

Manual rollback of database changes should be performed only by
authorized personnel following internal procedures.

------------------------------------------------------------------------

## Security Contact

For security-related questions, issues, or disclosures, please open a
private issue or contact the maintainer.
