# Database Schema — CYPR Tech

CYPR Tech maps application data to a PostgreSQL relational database using Hibernate JPA ORM.

## Relational Schema Mappings

### 1. `users` Table
Stores core credentials and verification flags.
- `id` (BIGINT, Primary Key)
- `email` (VARCHAR, Unique)
- `password_hash` (VARCHAR)
- `full_name` (VARCHAR)
- `enabled` (BOOLEAN) — default `false`. Pre-existing users before email verification was introduced are migrated to `true` automatically at startup.
- `profile_pic_url` (TEXT) — evolved from VARCHAR to TEXT type to prevent length overflow when storing base64 or long cloud asset links.
- `created_at` (TIMESTAMP)

### 2. `scan_history` Table
Stores URL risk ratings and scores.
- `id` (BIGINT, Primary Key)
- `user_id` (BIGINT, Foreign Key referencing users.id)
- `url` (TEXT)
- `score` (INT)
- `tier` (VARCHAR) — mapped to threat categories (`SAFE`, `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`)
- `timestamp` (TIMESTAMP)

### 3. `security_alerts` Table
Stores telemetry events for security events and live threat feeds.
- `id` (BIGINT, Primary Key)
- `title` (VARCHAR)
- `description` (TEXT)
- `result` (VARCHAR)
- `timestamp` (TIMESTAMP)

### 4. `email_logs` Table
Keeps record of transaction mailings.
- `id` (BIGINT, Primary Key)
- `recipient` (VARCHAR)
- `subject` (VARCHAR)
- `body` (TEXT)
- `sent_at` (TIMESTAMP)

---

## Schema Evolution & Migration Hooks

At application boot, the `DatabaseInitializer` service runs custom queries to perform safe updates to Postgres schemas:
- **`profile_pic_url` Modification**: Modifies column types to `TEXT` type to handle larger strings safely.
- **`enabled` Column Check**: Adds the boolean `enabled` column if missing from legacy structures, automatically enabling pre-existing developers and active accounts.
