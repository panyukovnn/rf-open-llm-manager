#!/bin/bash
set -e

BACKUP_DIR="/home/tech/rf-open-llm/backups"
CONTAINER="postgres"
DB_USER="${POSTGRES_USER:-billing_manager}"
DB_NAME="${POSTGRES_DB:-billing_manager}"
TIMESTAMP=$(date +%Y%m%d_%H%M)

mkdir -p "$BACKUP_DIR"

docker exec "$CONTAINER" pg_dump -U "$DB_USER" "$DB_NAME" \
  | gzip > "$BACKUP_DIR/billing_${TIMESTAMP}.sql.gz"

# Удалить бэкапы старше 14 дней
find "$BACKUP_DIR" -name "*.sql.gz" -mtime +14 -delete

echo "Backup completed: billing_${TIMESTAMP}.sql.gz"
