# Database Migrations with Flyway

This document explains the database migration setup for the Travel360 API using Flyway.

## Overview

Database migrations are essential for:
- **Version Control**: Track database schema changes alongside code changes
- **Consistency**: Ensure all environments have the same database structure
- **Rollback**: Ability to revert database changes if needed
- **Team Collaboration**: Share database changes across development teams
- **Deployment**: Automated database updates during application deployment

## Flyway Configuration

### Dependencies
The following dependency is added to `pom.xml`:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

### Application Properties
```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0
spring.flyway.validate-on-migrate=true

# JPA Configuration (important!)
spring.jpa.hibernate.ddl-auto=validate
```

**Important**: Set `spring.jpa.hibernate.ddl-auto=validate` to prevent Hibernate from auto-generating schema changes.

## Migration Files

### Naming Convention
Migration files must follow this pattern:
```
V{version}__{description}.sql
```

Examples:
- `V1__Create_users_table.sql`
- `V2__Create_airports_table.sql`
- `V3__Add_index_to_flights.sql`

### Current Migrations

| Version | File | Description |
|---------|------|-------------|
| V1 | `V1__Create_users_table.sql` | Creates users table with roles and audit fields |
| V2 | `V2__Create_airports_table.sql` | Creates airports table with IATA codes |
| V3 | `V3__Create_airplanes_table.sql` | Creates airplanes table with capacity info |
| V4 | `V4__Create_flights_table.sql` | Creates flights table with pricing and status |
| V5 | `V5__Create_bookings_table.sql` | Creates bookings table for reservations |
| V6 | `V6__Create_booking_details_table.sql` | Creates booking details for passengers |
| V7 | `V7__Insert_sample_data.sql` | Inserts sample data for testing |

## Migration History

Flyway tracks migration history in the `flyway_schema_history` table:

```sql
-- View migration history
SELECT * FROM flyway_schema_history ORDER BY installed_on;
```

## Best Practices

### 1. Never Modify Existing Migrations
Once a migration is applied, **never modify it**. Create a new migration instead.

### 2. Use Descriptive Names
Use clear, descriptive names for migration files.

### 3. Test Migrations
Always test migrations on a copy of production data before applying to production.

## Sample Data

The application includes sample data with test users and flight information for development and testing purposes. 