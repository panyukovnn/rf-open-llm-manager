#!/bin/bash
# Запускать от root на сервере abots
# Использование: bash setup-server.sh
set -e

PROJECT_DIR="/home/tech/rf-open-llm"

echo "=== 1. Создание БД ==="
docker exec postgres psql -U postgres -c "
  DO \$\$
  BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'billing_manager') THEN
      CREATE USER billing_manager WITH PASSWORD '${BILLING_MANAGER_DB_PASSWORD}';
    END IF;
  END
  \$\$;
  CREATE DATABASE billing_manager OWNER billing_manager;
" || echo "БД уже существует, пропускаем"

echo "=== 2. Nginx конфиг ==="
cp "$PROJECT_DIR/deploy/nginx-rf-open-llm.ru.conf" /etc/nginx/sites-available/rf-open-llm.ru
ln -sf /etc/nginx/sites-available/rf-open-llm.ru /etc/nginx/sites-enabled/rf-open-llm.ru
nginx -t

echo "=== 3. TLS сертификат ==="
certbot certonly --nginx -d rf-open-llm.ru -d www.rf-open-llm.ru --non-interactive --agree-tos -m "${CERTBOT_EMAIL}"

echo "=== 4. Reload nginx ==="
systemctl reload nginx

echo "=== 5. Запуск контейнеров ==="
cd "$PROJECT_DIR"
docker compose -f docker-compose.prod.yml up -d --build

echo "=== 6. Bэкап cron ==="
CRON_JOB="0 4 * * * /home/tech/rf-open-llm/deploy/backup-postgres.sh"
(crontab -l 2>/dev/null | grep -v "backup-postgres" ; echo "$CRON_JOB") | crontab -
chmod +x "$PROJECT_DIR/deploy/backup-postgres.sh"

echo "=== Готово ==="
echo "Проверь: curl https://rf-open-llm.ru/billing-manager/actuator/health"
