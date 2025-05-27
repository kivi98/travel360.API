#!/bin/bash

# Travel360 API - Database Migration Helper Script
# This script provides convenient commands for managing Flyway database migrations

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if PostgreSQL is running
check_postgres() {
    print_info "Checking PostgreSQL connection..."
    if pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
        print_success "PostgreSQL is running"
        return 0
    else
        print_error "PostgreSQL is not running or not accessible"
        print_info "Please start PostgreSQL and ensure the database 'travel360db' exists"
        return 1
    fi
}

# Function to show migration status
show_status() {
    print_info "Checking migration status..."
    mvn flyway:info
}

# Function to run migrations
migrate() {
    print_info "Running database migrations..."
    mvn flyway:migrate -q
    if [ $? -eq 0 ]; then
        print_success "Migrations completed successfully"
        show_status
    else
        print_error "Migration failed"
        exit 1
    fi
}

# Function to validate migrations
validate() {
    print_info "Validating migrations..."
    mvn flyway:validate -q
    if [ $? -eq 0 ]; then
        print_success "All migrations are valid"
    else
        print_error "Migration validation failed"
        exit 1
    fi
}

# Function to clean database (development only)
clean() {
    print_warning "This will DELETE ALL DATA in the database!"
    read -p "Are you sure you want to continue? (type 'yes' to confirm): " confirm
    if [ "$confirm" = "yes" ]; then
        print_info "Cleaning database..."
        mvn flyway:clean -q
        print_success "Database cleaned"
    else
        print_info "Operation cancelled"
    fi
}

# Function to repair migrations
repair() {
    print_info "Repairing migration metadata..."
    mvn flyway:repair -q
    if [ $? -eq 0 ]; then
        print_success "Migration metadata repaired"
    else
        print_error "Repair failed"
        exit 1
    fi
}

# Function to create a new migration file
create_migration() {
    if [ -z "$1" ]; then
        print_error "Please provide a migration description"
        echo "Usage: $0 create <description>"
        echo "Example: $0 create Add_user_preferences_table"
        exit 1
    fi
    
    # Get the next version number
    MIGRATION_DIR="src/main/resources/db/migration"
    LAST_VERSION=$(ls -1 $MIGRATION_DIR/V*.sql 2>/dev/null | sed 's/.*V\([0-9]*\)__.*/\1/' | sort -n | tail -1)
    
    if [ -z "$LAST_VERSION" ]; then
        NEXT_VERSION=1
    else
        NEXT_VERSION=$((LAST_VERSION + 1))
    fi
    
    DESCRIPTION=$(echo "$1" | sed 's/ /_/g')
    FILENAME="V${NEXT_VERSION}__${DESCRIPTION}.sql"
    FILEPATH="$MIGRATION_DIR/$FILENAME"
    
    # Create the migration file with template
    cat > "$FILEPATH" << EOF
-- Migration: $DESCRIPTION
-- Version: V$NEXT_VERSION
-- Created: $(date '+%Y-%m-%d %H:%M:%S')

-- Add your SQL statements here
-- Example:
-- CREATE TABLE example_table (
--     id BIGSERIAL PRIMARY KEY,
--     name VARCHAR(100) NOT NULL,
--     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
-- );

-- Remember to:
-- 1. Test this migration on a copy of production data
-- 2. Consider the impact on existing data
-- 3. Add appropriate indexes for performance
-- 4. Document any rollback procedures in comments
EOF
    
    print_success "Created migration file: $FILEPATH"
    print_info "Edit the file to add your SQL statements"
}

# Function to show help
show_help() {
    echo "Travel360 API - Database Migration Helper"
    echo ""
    echo "Usage: $0 <command> [options]"
    echo ""
    echo "Commands:"
    echo "  status     - Show current migration status"
    echo "  migrate    - Run pending migrations"
    echo "  validate   - Validate migration files"
    echo "  clean      - Clean database (DEVELOPMENT ONLY - DELETES ALL DATA)"
    echo "  repair     - Repair migration metadata"
    echo "  create     - Create a new migration file"
    echo "  check      - Check PostgreSQL connection"
    echo "  help       - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 status"
    echo "  $0 migrate"
    echo "  $0 create Add_user_preferences_table"
    echo ""
    echo "Prerequisites:"
    echo "  - PostgreSQL must be running on localhost:5432"
    echo "  - Database 'travel360db' must exist"
    echo "  - Maven must be installed"
}

# Main script logic
case "$1" in
    "status")
        check_postgres && show_status
        ;;
    "migrate")
        check_postgres && migrate
        ;;
    "validate")
        validate
        ;;
    "clean")
        check_postgres && clean
        ;;
    "repair")
        check_postgres && repair
        ;;
    "create")
        create_migration "$2"
        ;;
    "check")
        check_postgres
        ;;
    "help"|"--help"|"-h"|"")
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac 